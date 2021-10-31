package com.github.jeffreyning.mybatisplus.base;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.github.jeffreyning.mybatisplus.handler.DataAutoFill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author ninghao
 */
public class MppSqlInjector extends DefaultSqlInjector {
    private static final Logger logger = LoggerFactory.getLogger(MppSqlInjector.class);
    //modify 1.6.0
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new SelectByMultiIdMethod());
        methodList.add(new UpdateByMultiIdMethod());
        methodList.add(new DeleteByMultiIdMethod());
        return methodList;
    }
/*    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new SelectByMultiIdMethod());
        methodList.add(new UpdateByMultiIdMethod());
        methodList.add(new DeleteByMultiIdMethod());
        return methodList;
    }*/
}
