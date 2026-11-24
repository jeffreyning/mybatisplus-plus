package com.github.jeffreyning.mybatisplus.util;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CheckId {
    //add 1.7.2
    public static Field getIdField(Class<?> modelClass, String fieldName){
        try {
            Field field=modelClass.getDeclaredField(fieldName);
            return field;
        } catch (NoSuchFieldException e) {
            Class superclass=modelClass.getSuperclass();
            if(superclass!=null) {
                Field superField = getIdField(superclass, fieldName);
                return superField;
            }else{
                return null;
            }
        }
    }
    //add 1.7.2
    public static boolean appendIdColum(Class<?> modelClass, TableInfo tableInfo, Map idMap){
        String keycol=tableInfo.getKeyColumn();
        String keypro=tableInfo.getKeyProperty();
        if(StringUtils.checkValNull(keypro)){
            return false;
        }
        Field idField=getIdField(modelClass, keypro);
        if(idField!=null){
            MppMultiId mppMultiId=idField.getAnnotation(MppMultiId.class);
            if(mppMultiId!=null){
                String attrName=keypro;
                String colName=keycol;
                idMap.put(attrName, colName);
                return true;
            }
        }
        return false;
    }

    public static Map<String, String> getMultiIdMap(Class<?> modelClass, TableInfo tableInfo){
        Map<String, String> idMap=new LinkedHashMap<String, String>();
        if(tableInfo==null){
            return idMap;
        }
        List<TableFieldInfo> fieldList=tableInfo.getFieldList();
        for(TableFieldInfo fieldInfo: fieldList){
            Field field=fieldInfo.getField();
            if(field.getAnnotation(MppMultiId.class)!=null){
                idMap.put(field.getName(), fieldInfo.getColumn());
            }
        }
        appendIdColum(modelClass, tableInfo, idMap);
        return idMap;
    }

    public static Object getIdValue(Class<?> modelClass, String fieldName, Object entity){
        Field field=getIdField(modelClass, fieldName);
        if(field==null){
            return null;
        }
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("can not read multi id field "+fieldName+" of "+modelClass.getName(), e);
        }
    }

    public static boolean hasNullId(Class<?> modelClass, TableInfo tableInfo, Object entity){
        if(entity==null){
            return true;
        }
        Map<String, String> idMap=getMultiIdMap(modelClass, tableInfo);
        for(String attrName: idMap.keySet()){
            if(StringUtils.checkValNull(getIdValue(modelClass, attrName, entity))){
                return true;
            }
        }
        return false;
    }

    public static void checkIdNotNull(Class<?> modelClass, TableInfo tableInfo, Object entity){
        if(entity==null){
            throw new IllegalArgumentException("multi id entity must not be null");
        }
        Map<String, String> idMap=getMultiIdMap(modelClass, tableInfo);
        for(String attrName: idMap.keySet()){
            if(StringUtils.checkValNull(getIdValue(modelClass, attrName, entity))){
                throw new IllegalArgumentException("multi id field "+attrName+" of "+modelClass.getName()+" must not be null");
            }
        }
    }

    public static void checkIdNotNull(Object entity){
        if(entity==null){
            throw new IllegalArgumentException("multi id entity must not be null");
        }
        Class<?> modelClass=entity.getClass();
        TableInfo tableInfo=TableInfoHelper.getTableInfo(modelClass);
        if(tableInfo==null){
            throw new IllegalStateException("error: can not execute. because can not find cache of TableInfo for entity!");
        }
        checkIdNotNull(modelClass, tableInfo, entity);
    }
}
