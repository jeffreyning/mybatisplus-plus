package com.github.jeffreyning.mybatisplus.base;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.jeffreyning.mybatisplus.handler.DataAutoFill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author ninghao
 */
public class MppSqlInjector extends DefaultSqlInjector {
    private static final Logger logger = LoggerFactory.getLogger(MppSqlInjector.class);
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new SelectByMultiIdMethod());
        return methodList;
    }
}
