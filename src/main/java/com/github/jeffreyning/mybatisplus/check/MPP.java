package com.github.jeffreyning.mybatisplus.check;

import com.github.jeffreyning.mybatisplus.util.LambdaUtil;

/**
 * @author ninghao
 */
public class MPP {
    public static String col(String lamdbaFunc){
        return LambdaUtil.parseFunc(lamdbaFunc);
    }
    public static String trim(String qwSql){
        if(qwSql==null || qwSql.length()<=0){
            return qwSql;
        }
        if(qwSql.startsWith("where") || qwSql.startsWith("WHERE")){
            return qwSql.substring(5);
        }
        return qwSql;
    }
}
