package com.github.jeffreyning.mybatisplus.service;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.github.jeffreyning.mybatisplus.util.CheckId;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Field;
import java.util.*;


/**
 * @author ninghao
 */
public class MppServiceImpl<M extends MppBaseMapper<T>, T> extends ServiceImpl<M, T> implements IMppService<T> {

    private String getCol(List<TableFieldInfo> fieldList, String attrName){
        for(TableFieldInfo tableFieldInfo: fieldList){
            String prop=tableFieldInfo.getProperty();
            if(prop.equals(attrName)){
                return tableFieldInfo.getColumn();
            }
        }
        throw new RuntimeException("not found column for "+attrName);
    }

    private Map checkIdCol(Class<?> modelClass, TableInfo tableInfo){
        List<TableFieldInfo> fieldList=tableInfo.getFieldList();
        Map<String, String> idMap=new HashMap();
        for(TableFieldInfo fieldInfo: fieldList){
            Field field=fieldInfo.getField();
            MppMultiId mppMultiId= field.getAnnotation(MppMultiId.class);
            if(mppMultiId!=null){
                String attrName=field.getName();
                String colName=getCol(fieldList, attrName);
                idMap.put(attrName, colName);
            }
        }
        //add 1.7.2
        CheckId.appendIdColum(modelClass,tableInfo,idMap);
        return idMap;
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

            boolean updateFlag=true;
            for(String attr: idMap.keySet()){
                if(StringUtils.checkValNull(attr)){
                    updateFlag=false;
                    break;
                }
            }
            if(updateFlag){
                Object obj=this.selectByMultiId(entity);
                if(Objects.isNull(obj)){
                   updateFlag=false;
                }
            }
            return updateFlag ? this.updateByMultiId(entity) : this.save(entity);
        }
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean saveOrUpdateBatchByMultiId(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);

        Map<String, String> idMap=checkIdCol(this.entityClass, tableInfo);
        Assert.notEmpty(idMap, "entity {} not contain MppMultiId anno", new Object[]{this.entityClass.getName()});

        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            boolean updateFlag=true;
            for(String attr: idMap.keySet()){
                if(StringUtils.checkValNull(attr)){
                    updateFlag=false;
                    break;
                }
            }
            if(updateFlag){
                Object obj=this.selectByMultiId(entity);
                if(Objects.isNull(obj)){
                    updateFlag=false;
                }
            }
            if (updateFlag) {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
                param.put("et", entity);
                sqlSession.update(tableInfo.getSqlStatement("updateByMultiId"), param);
            } else {
                sqlSession.insert(tableInfo.getSqlStatement(SqlMethod.INSERT_ONE.getMethod()), entity);
            }

        });
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean updateBatchByMultiId(Collection<T> entityList, int batchSize) {
        String sqlStatement = SqlHelper.table(this.entityClass).getSqlStatement("updateByMultiId");
        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
            param.put("et", entity);
            sqlSession.update(sqlStatement, param);
        });
    }
}
