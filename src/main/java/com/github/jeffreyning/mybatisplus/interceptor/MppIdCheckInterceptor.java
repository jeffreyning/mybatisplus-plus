package com.github.jeffreyning.mybatisplus.interceptor;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.jeffreyning.mybatisplus.util.CheckId;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Map;
import java.util.Properties;

/**
 * @author ninghao
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class MppIdCheckInterceptor implements Interceptor {
    private static final String[] METHOD_SUFFIXES = {".selectByMultiId", ".updateByMultiId", ".deleteByMultiId"};

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        if (isMultiIdStatement(mappedStatement.getId())) {
            Object entity = getEntity(args[1]);
            if (entity == null) {
                throw new IllegalArgumentException("multi id entity must not be null");
            }
            TableInfo tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
            if (tableInfo != null) {
                CheckId.checkIdNotNull(entity.getClass(), tableInfo, entity);
            }
        }
        return invocation.proceed();
    }

    private boolean isMultiIdStatement(String statementId) {
        for (String suffix : METHOD_SUFFIXES) {
            if (statementId.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private Object getEntity(Object parameterObject) {
        if (parameterObject instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) parameterObject;
            if (paramMap.containsKey("et")) {
                return paramMap.get("et");
            }
            if (paramMap.size() == 1) {
                return paramMap.values().iterator().next();
            }
            return null;
        }
        return parameterObject;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
