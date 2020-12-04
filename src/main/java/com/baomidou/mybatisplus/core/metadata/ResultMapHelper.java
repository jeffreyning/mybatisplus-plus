package com.baomidou.mybatisplus.core.metadata;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

/**
 * @author ninghao
 */
public class ResultMapHelper {
    public static TableInfo createResultMap(Configuration configuration, Class cls){
        MapperBuilderAssistant assistant=new MapperBuilderAssistant(configuration, "");
        assistant.setCurrentNamespace("scan");
        TableInfo tableInfo = TableInfoHelper.initTableInfo(assistant, cls);
        return tableInfo;
    }
}
