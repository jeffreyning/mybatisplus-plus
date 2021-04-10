package com.github.jeffreyning.mybatisplus.conf;

import com.github.jeffreyning.mybatisplus.base.MppSqlInjector;
import com.github.jeffreyning.mybatisplus.handler.DataAutoFill;
import com.github.jeffreyning.mybatisplus.ognl.NhOgnlClassResolver;
import com.github.jeffreyning.mybatisplus.scan.ResultMapUtil;
import com.github.jeffreyning.mybatisplus.scan.ScanUtil;
import com.github.jeffreyning.mybatisplus.util.LambdaUtil;
import com.github.jeffreyning.mybatisplus.util.PlusACUtils;
import org.apache.ibatis.scripting.xmltags.OgnlCache;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author ninghao
 */
//@Import({DataAutoFill.class, PlusACUtils.class, MppSqlInjector.class})
//modify 1.5.1
@Import({PlusACUtils.class, MppSqlInjector.class})
@Configuration
public class PlusConfig {
    private static final Logger logger = LoggerFactory.getLogger(PlusConfig.class);
    //@Autowired
    //private  PlusACUtils plusACUtils;
    @Autowired
    private Environment env;

/*    @Bean
    public MppSqlInjector mppSqlInjector(){
        return new MppSqlInjector();
    }*/

    @PostConstruct
    public void initRM() throws Exception {
        NhOgnlClassResolver resolver=new NhOgnlClassResolver();
        resolver.baseList.add("com.github.jeffreyning.mybatisplus.check");
        LambdaUtil.setValue(OgnlCache.class,"CLASS_RESOLVER",resolver);
        String utilBasePaths=env.getProperty("mpp.utilBasePath");
        if(utilBasePaths==null || "".equals(utilBasePaths) ){
            logger.info("mpp.utilBasePath is null no util alias for xml");
        }else{
            String[] utilPaths= utilBasePaths.split(",");
            for(String up:utilPaths){
                resolver.baseList.add(up);
            }
        }

        String basePaths=env.getProperty("mpp.entityBasePath");
        if(basePaths==null || "".equals(basePaths) ){
            logger.error("mpp.entityBasePath is null skip scan result map");
            return;
        }
        String[] paths= basePaths.split(",");
        List<String> pathList= Arrays.asList(paths);
        for(String base:pathList){
            Set<Class> set= ScanUtil.getClasses(base);
            for(Class cls:set){
                ResultMapUtil.createResultMap(cls);
                LambdaUtil.createColDict(cls);
            }
        }

    }
}
