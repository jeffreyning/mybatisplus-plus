package com.github.jeffreyning.mybatisplus.conf;

import com.github.jeffreyning.mybatisplus.interceptor.MppIdCheckInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ninghao
 */
@Configuration
public class MppIdCheckConfig {
    @Bean
    public MppIdCheckInterceptor mppIdCheckInterceptor(){
        return new MppIdCheckInterceptor();
    }
}
