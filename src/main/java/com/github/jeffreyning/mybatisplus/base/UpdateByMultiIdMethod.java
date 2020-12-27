package com.github.jeffreyning.mybatisplus.base;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
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
public class UpdateByMultiIdMethod extends AbstractMethod {
    private static final Logger logger = LoggerFactory.getLogger(UpdateByMultiIdMethod.class);
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
        Field[] fieldArray= modelClass.getDeclaredFields();
        Map<String, String> idMap=new HashMap();
        for(Field field: fieldArray){
            MppMultiId mppMultiId= field.getAnnotation(MppMultiId.class);
            if(mppMultiId!=null){
                String attrName=field.getName();
                String colName=getCol(fieldList, attrName);
                idMap.put(attrName, colName);
            }
        }
        if(idMap.isEmpty()){
            logger.info("entity {} not contain MppMultiId anno", modelClass.getName());
            return null;
        }
        StringBuilder sb=new StringBuilder("");
        idMap.forEach((attrName, colName)->{
            if(sb.length() <=0){

            }else{
                sb.append(" and ");
            }
            sb.append(colName).append("=").append("#{et.").append(attrName).append("}");
        });
        return sb.toString();
    }
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String cWhere=createWhere(modelClass, tableInfo);
        if(cWhere==null){
            return null;
        }
        String methodName="updateByMultiId";
        boolean logicDelete = tableInfo.isLogicDelete();
        String sqlTemp="<script>\nUPDATE %s %s WHERE "+cWhere+" %s\n</script>";
        String additional = this.optlockVersion(tableInfo) + tableInfo.getLogicDeleteSql(true, true);
        String sql = String.format(sqlTemp, tableInfo.getTableName(), this.sqlSet(logicDelete, false, tableInfo, false, "et", "et."), additional);
        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, methodName, sqlSource);
    }
}
