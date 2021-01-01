package com.github.jeffreyning.mybatisplus.plugin;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ninghao
 */
@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
)})
public class MppPaginationInterceptor extends PaginationInterceptor {
    private DbType dbType;
    private IDialect dialect;
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        this.sqlParser(metaObject);
        MappedStatement mappedStatement = (MappedStatement)metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.SELECT == mappedStatement.getSqlCommandType() && StatementType.CALLABLE != mappedStatement.getStatementType()) {
            BoundSql boundSql = (BoundSql)metaObject.getValue("delegate.boundSql");
            Object paramObj = boundSql.getParameterObject();
            IPage<?> page = (IPage) ParameterUtils.findPage(paramObj).orElse((IPage) null);
            if (null != page && page.getSize() >= 0L) {
                if (this.limit > 0L && this.limit <= page.getSize()) {
                    this.handlerLimit(page);
                }

                String originalSql = boundSql.getSql();
                Connection connection = (Connection)invocation.getArgs()[0];
                if (page.isSearchCount() && !page.isHitCount()) {
                    SqlInfo sqlInfo = SqlParserUtils.getOptimizeCountSql(page.optimizeCountSql(), this.countSqlParser, originalSql);
                    this.queryTotal(sqlInfo.getSql(), mappedStatement, boundSql, page, connection);
                    if (page.getTotal() <= 0L) {
                        return null;
                    }
                }

                DbType dbType = this.dbType == null ? JdbcUtils.getDbType(connection.getMetaData().getURL()) : this.dbType;
                IDialect dialect = (IDialect) Optional.ofNullable(this.dialect).orElseGet(() -> {
                    return DialectFactory.getDialect(dbType);
                });
                String buildSql = this.concatOrderBy(originalSql, page);
                DialectModel model = dialect.buildPaginationSql(buildSql, page.offset(), page.getSize());
                Configuration configuration = mappedStatement.getConfiguration();
                List<ParameterMapping> mappings = new ArrayList(boundSql.getParameterMappings());
                Map<String, Object> additionalParameters = (Map)metaObject.getValue("delegate.boundSql.additionalParameters");
                model.consumers(mappings, configuration, additionalParameters);
                metaObject.setValue("delegate.boundSql.sql", model.getDialectSql());
                metaObject.setValue("delegate.boundSql.parameterMappings", mappings);
                return invocation.proceed();
            } else if(page!=null) {
                String originalSql = boundSql.getSql();
                String buildSql = this.concatOrderBy(originalSql, page);
                List<ParameterMapping> mappings = new ArrayList(boundSql.getParameterMappings());
                metaObject.setValue("delegate.boundSql.sql", buildSql);
                metaObject.setValue("delegate.boundSql.parameterMappings", mappings);
                return invocation.proceed();
            } else {
                return invocation.proceed();
            }
        } else {
            return invocation.proceed();
        }
    }
}
