package com.github.jeffreyning.mybatisplus.util;


import org.apache.ibatis.type.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;

/**
 * 自动填充时sql返回值与entity字段类型转换
 * @author ninghao
 * @version 1.7.4
 */
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
        }else if(fieldType.equals(String.class)) {
            StringTypeHandler typeHandler=new StringTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(BigInteger.class)) {
            BigIntegerTypeHandler typeHandler=new BigIntegerTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(Character.class)) {
            CharacterTypeHandler typeHandler=new CharacterTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(Date.class)) {
            DateTypeHandler typeHandler=new DateTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(LocalDateTime.class)) {
            LocalDateTimeTypeHandler typeHandler=new LocalDateTimeTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(LocalDate.class)) {
            LocalDateTypeHandler typeHandler=new LocalDateTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(LocalTime.class)) {
            LocalTimeTypeHandler typeHandler=new LocalTimeTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(Month.class)) {
            MonthTypeHandler typeHandler=new MonthTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(java.sql.Date.class)) {
            SqlDateTypeHandler typeHandler=new SqlDateTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(java.sql.Timestamp.class)) {
            SqlTimestampTypeHandler typeHandler=new SqlTimestampTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(java.sql.Time.class)) {
            SqlTimeTypeHandler typeHandler=new SqlTimeTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(YearMonth.class)) {
            YearMonthTypeHandler typeHandler=new YearMonthTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(Year.class)) {
            YearTypeHandler typeHandler=new YearTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else if(fieldType.equals(ZonedDateTime.class)) {
            ZonedDateTimeTypeHandler typeHandler=new ZonedDateTimeTypeHandler();
            return typeHandler.getNullableResult(rs,1);
        }else{
            return rs.getObject(1);
        }
    }
}
