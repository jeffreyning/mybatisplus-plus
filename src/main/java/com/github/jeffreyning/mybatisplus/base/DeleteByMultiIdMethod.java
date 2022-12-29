package com.github.jeffreyning.mybatisplus.base;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import com.github.jeffreyning.mybatisplus.util.CheckId;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ninghao
 */
public class DeleteByMultiIdMethod extends AbstractMethod {
    private static final Logger logger = LoggerFactory.getLogger(DeleteByMultiIdMethod.class);
    private String getCol(List<TableFieldInfo> fieldList, String attrName){
        for(TableFieldInfo tableFieldInfo: fieldList){
            String prop=tableFieldInfo.getProperty();
            if(prop.equals(attrName)){
                return tableFieldInfo.getColumn();
            }
        }
        throw new RuntimeException("not found column for "+attrName);
    }
    private String createWhere(Class<?> modelClass, TableInfo tableInfo){
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
        if(idMap.isEmpty()){
            logger.info("entity {} not contain MppMultiId anno", modelClass.getName());
            return null;
        }
        StringBuilder sb=new StringBuilder("");
        idMap.forEach((attrName, colName)->{
            if(sb.length() >0){
                sb.append(" and ");
            }
            sb.append(colName).append("=").append("#{").append(attrName).append("}");
        });
        return sb.toString();
    }
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String cWhere=createWhere(modelClass, tableInfo);
        if(cWhere==null){
            return null;
        }
        String methodName="deleteByMultiId";
        SqlSource sqlSource;
        if (tableInfo.isLogicDelete()) {
            String sqlTemp="<script>\nUPDATE %s %s WHERE "+cWhere+" %s\n</script>";
            String sql = String.format(sqlTemp, tableInfo.getTableName(), this.sqlLogicSet(tableInfo), tableInfo.getLogicDeleteSql(true, true));
            sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, Object.class);
            return this.addUpdateMappedStatement(mapperClass, modelClass, methodName, sqlSource);
        } else {
            String sqlTemp = "DELETE FROM %s WHERE "+cWhere;
            String sql = String.format(sqlTemp, tableInfo.getTableName());
            sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, Object.class);
            return this.addDeleteMappedStatement(mapperClass, methodName, sqlSource);
        }
    }
}
