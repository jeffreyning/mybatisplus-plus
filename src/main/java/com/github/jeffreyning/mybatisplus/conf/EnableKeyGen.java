package com.github.jeffreyning.mybatisplus.conf;

import com.github.jeffreyning.mybatisplus.handler.MppKeyGenerator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author ninghao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MppKeyGenerator.class)
public @interface EnableKeyGen {
}
