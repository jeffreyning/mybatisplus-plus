package com.github.jeffreyning.mybatisplus.service;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.github.jeffreyning.mybatisplus.util.CheckId;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;


/**
 * @author ninghao
 */
public class MppServiceImpl<M extends MppBaseMapper<T>, T> extends ServiceImpl<M, T> implements IMppService<T> {

    private Map<String, String> checkIdCol(Class<?> modelClass, TableInfo tableInfo){
        return CheckId.getMultiIdMap(modelClass, tableInfo);
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean saveOrUpdateByMultiId(T entity) {
        if (null == entity) {
            return false;
        } else {
            Class<?> cls = entity.getClass();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);

            Map<String, String> idMap=checkIdCol(cls, tableInfo);
            Assert.notEmpty(idMap, "entity {} not contain MppMultiId anno", new Object[]{cls.getName()});
            CheckId.checkIdNotNull(cls, tableInfo, entity);

            Object obj=this.selectByMultiId(entity);
            return Objects.isNull(obj) ? this.save(entity) : this.updateByMultiId(entity);
        }
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean saveOrUpdateBatchByMultiId(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);

        Map<String, String> idMap=checkIdCol(this.getEntityClass(), tableInfo);
        Assert.notEmpty(idMap, "entity {} not contain MppMultiId anno", new Object[]{this.getEntityClass().getName()});
        if(entityList!=null){
            for(T entity: entityList){
                CheckId.checkIdNotNull(this.getEntityClass(), tableInfo, entity);
            }
        }

        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            Object obj=this.selectByMultiId(entity);
            if (Objects.isNull(obj)) {
                return sqlSession.insert(tableInfo.getSqlStatement(SqlMethod.INSERT_ONE.getMethod()), entity);
            } else {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
                param.put("et", entity);
                return sqlSession.update(tableInfo.getSqlStatement("updateByMultiId"), param);
            }

        });
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean updateBatchByMultiId(Collection<T> entityList, int batchSize) {
        if(entityList!=null){
            for(T entity: entityList){
                CheckId.checkIdNotNull(entity);
            }
        }
        String sqlStatement = SqlHelper.table(this.getEntityClass()).getSqlStatement("updateByMultiId");
        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
            param.put("et", entity);
            return sqlSession.update(sqlStatement, param);
        });
    }
}
