package com.github.jeffreyning.mybatisplus.check;

import com.github.jeffreyning.mybatisplus.util.LambdaUtil;

/**
 * @author ninghao
 */
public class MPP {
    public static String col(String lamdbaFunc){
        return LambdaUtil.parseFunc(lamdbaFunc);
    }
}
