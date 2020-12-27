package com.github.jeffreyning.mybatisplus.anno;

import java.lang.annotation.*;

/**
 * @author ninghao
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MppMultiId {
    String value() default "";
}
