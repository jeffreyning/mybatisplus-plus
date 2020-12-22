package com.github.jeffreyning.mybatisplus.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * @author ninghao
 */
public class ColNameUtil {
    public static <T> String pn(ParseFun<T, Object> parseFun) throws Exception {
        Method writeReplace = parseFun.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        Object sl = writeReplace.invoke(parseFun);
        SerializedLambda serializedLambda = (SerializedLambda) sl;
        String className=serializedLambda.getImplClass();
        className=className.replace("/",".");
        String methodName = serializedLambda.getImplMethodName();
        if (methodName.startsWith("is")) {
            methodName = methodName.substring(2);
        } else {
            if (!methodName.startsWith("get") && !methodName.startsWith("set")) {
                throw new IllegalArgumentException("Error parsing property name '" + methodName
                        + "'.  Didn't start with 'is', 'get' or 'set'.");
            }
            methodName = methodName.substring(3);
        }
        if (methodName.length() == 1 || (methodName.length() > 1 && !Character
                .isUpperCase(methodName.charAt(1)))) {
            methodName = methodName.substring(0, 1).toLowerCase(Locale.ENGLISH) + methodName.substring(1);
        }
        return getColName(className, methodName);
    }
    private static String getColName(String className, String attrName) throws Exception {
        Class cls=Class.forName(className);
        Field field=cls.getDeclaredField(attrName);
        TableField tableField=field.getAnnotation(TableField.class);
        if(tableField!=null){
            return tableField.value();
        }
        TableId tableId=field.getAnnotation(TableId.class);
        if(tableId!=null){
            return tableId.value();
        }
        throw new Exception("can not found colname for "+className+"."+attrName);
    }
}
