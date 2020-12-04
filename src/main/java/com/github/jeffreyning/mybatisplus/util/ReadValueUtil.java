package com.github.jeffreyning.mybatisplus.util;


import org.apache.ibatis.type.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ReadValueUtil {

    public static Object readValue(ResultSet rs, Class fieldType) throws SQLException {
        if(fieldType.equals(Integer.class)) {
            IntegerTypeHandler typeHandler = new IntegerTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(Boolean.class)){
            BooleanTypeHandler typeHandler=new BooleanTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(Byte.class)) {
            ByteTypeHandler typeHandler=new ByteTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(Short.class)){
            ShortTypeHandler typeHandler=new ShortTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(Long.class)){
            LongTypeHandler typeHandler=new LongTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(Float.class)){
            FloatTypeHandler typeHandler=new FloatTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(Double.class)){
            DoubleTypeHandler typeHandler=new DoubleTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(BigDecimal.class)) {
            BigDecimalTypeHandler typeHandler=new BigDecimalTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else{
            return rs.getObject(1);
        }
    }
}
