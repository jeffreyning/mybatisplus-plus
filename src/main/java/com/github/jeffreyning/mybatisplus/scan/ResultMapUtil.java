package com.github.jeffreyning.mybatisplus.scan;

import com.baomidou.mybatisplus.core.metadata.ResultMapHelper;
import com.github.jeffreyning.mybatisplus.util.PlusACUtils;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;

/**
 * @author ninghao
 */
public class ResultMapUtil {
    public static void createResultMap(Class cls){
        SqlSessionTemplate sqlSessionTemplate = PlusACUtils.getBean(SqlSessionTemplate.class);
        Configuration configuration = sqlSessionTemplate.getConfiguration();
        ResultMapHelper.createResultMap(configuration, cls);
    }
}
