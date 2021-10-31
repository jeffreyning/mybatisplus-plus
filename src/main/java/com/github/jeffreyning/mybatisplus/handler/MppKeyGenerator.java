package com.github.jeffreyning.mybatisplus.handler;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import org.springframework.stereotype.Component;

/**
 * @author ninghao
 */
@Component("mppKeyGenerator")
public class MppKeyGenerator implements IKeyGenerator {
    @Override
    public String executeSql(String incrementerName) {
        return incrementerName;
    }

    //modify 1.6.0
    @Override
    public DbType dbType() {
        return null;
    }
}
