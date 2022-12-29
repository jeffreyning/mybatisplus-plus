package com.github.jeffreyning.mybatisplus.util;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;

import java.lang.reflect.Field;
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
}
