package com.github.jeffreyning.mybatisplus.ognl;

import org.apache.ibatis.scripting.xmltags.OgnlClassResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ninghao
 */
public class NhOgnlClassResolver extends OgnlClassResolver {
    public List<String> baseList=new ArrayList();
    public ConcurrentHashMap<String, Class> cacheCls = new ConcurrentHashMap(101);
    public Class classForName(String className, Map context) throws ClassNotFoundException {
        Class cls = (Class)this.cacheCls.get(className);
        if (cls != null) {
            return cls;
        } else {
            try {
                cls = super.classForName(className, context);
                return cls;
            } catch (ClassNotFoundException e) {
                for(String row:baseList){
                    String fullClassName = row+"." + className;
                    try {
                        cls = super.classForName(fullClassName, context);
                        if (cls != null) {
                            this.cacheCls.put(className, cls);
                            return cls;
                        }
                    }catch(ClassNotFoundException ex){

                    }
                };
                if(cls==null){
                    throw new ClassNotFoundException(className);
                }
                return cls;
            }
        }

    }
}
