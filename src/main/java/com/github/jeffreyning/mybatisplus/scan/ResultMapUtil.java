package com.github.jeffreyning.mybatisplus.scan;

import com.baomidou.mybatisplus.core.metadata.ResultMapHelper;
import com.github.jeffreyning.mybatisplus.anno.AutoMap;
import com.github.jeffreyning.mybatisplus.util.PlusACUtils;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;

import java.lang.annotation.Annotation;

/**
 * @author ninghao
 */
public class ResultMapUtil {
    public static void createResultMap(Class cls){
        Annotation[] anns=cls.getAnnotations();
        boolean flag=false;
        if(anns!=null) {
            for (Annotation an : anns) {
                if (an.toString().contains(AutoMap.class.getName())) {
                    flag=true;
                    break;
                }
            }
        }
        if (flag==false){
            return;
        }
        SqlSessionTemplate sqlSessionTemplate = PlusACUtils.getBean(SqlSessionTemplate.class);
        Configuration configuration = sqlSessionTemplate.getConfiguration();
        ResultMapHelper.createResultMap(configuration, cls);
    }
}
