package com.github.jeffreyning.mybatisplus.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class PlusACUtils implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(PlusACUtils.class);
    private static ApplicationContext context;


    public static <T> T getBean(Class<T> tClass) {
        try {
            return context.getBean(tClass);

        } catch (Exception e) {
            logger.error("can not get bean="+tClass.getName(), e);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
