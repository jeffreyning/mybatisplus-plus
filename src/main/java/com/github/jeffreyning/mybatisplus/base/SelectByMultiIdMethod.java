package com.github.jeffreyning.mybatisplus.base;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import com.github.jeffreyning.mybatisplus.conf.PlusConfig;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ninghao
 */
public class SelectByMultiIdMethod extends AbstractMethod {
    private static final Logger logger = LoggerFactory.getLogger(SelectByMultiIdMethod.class);
    private String getCol(List<TableFieldInfo> fieldList, String attrName){
        for(TableFieldInfo tableFieldInfo: fieldList){
            String prop=tableFieldInfo.getProperty();
            if(prop.equals(attrName)){
                return tableFieldInfo.getColumn();
            }
        }
        throw new RuntimeException("not found column for "+attrName);
    }
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
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
        }
        StringBuilder sb=new StringBuilder("");
        idMap.forEach((attrName, colName)->{
            if(sb.length() <=0){

            }else{
                sb.append(" and ");
            }
            sb.append(colName).append("=").append("#{").append(attrName).append("}");
        });

        //
        String sql = "SELECT %s FROM %s WHERE "+sb.toString()+" %s";

        String method = "selectByMultiId";

        SqlSource sqlSource = new RawSqlSource(this.configuration, String.format(sql, this.sqlSelectColumns(tableInfo, false), tableInfo.getTableName(), tableInfo.getLogicDeleteSql(true, true)), Object.class);
        return this.addSelectMappedStatementForTable(mapperClass, method, sqlSource, tableInfo);

    }

}
