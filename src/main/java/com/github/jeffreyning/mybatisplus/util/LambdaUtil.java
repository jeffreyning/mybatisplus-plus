package com.github.jeffreyning.mybatisplus.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ninghao
 */
public class LambdaUtil {
    private static Map<String,String> colDict=new HashMap<>();
    public static String parseFunc(String lamdbaFunc)  {
        String colName=colDict.get(lamdbaFunc);
        if(colName==null || "".equals(colName)){
            throw new RuntimeException("can not found colName for "+lamdbaFunc);
        }
        return colName;
    }
    //for jdk11 cancel ognl 202203 mpp1.7.0
/*    public static void setValue(Class cls, String fileName, Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = cls.getDeclaredField(fileName);
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(cls, value);
    }*/
    public static void createColDict(Class cls){
        Field[] fields=cls.getDeclaredFields();
        for (Field field:fields){
            TableId annoId=field.getAnnotation(TableId.class);
            if(annoId!=null){
                String colName=annoId.value();
                String fieldName=field.getName();
                putColDict(fieldName, colName, cls);
            }else {
                TableField anno = field.getAnnotation(TableField.class);
                if (anno == null) {
                    continue;
                }
                String colName=anno.value();
                String fieldName=field.getName();
                putColDict(fieldName, colName, cls);
            }
        }
    }
    private static void putColDict(String fieldName, String colName, Class cls){
        if(colName==null || "".equals(colName)){
            colName=fieldName;
        }
        String methodName="get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
        String clsName= cls.getSimpleName();
        String key=clsName+"::"+methodName;
        colDict.put(key, colName);
    }
}
