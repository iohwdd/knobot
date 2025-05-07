package com.iohw.knobot.common.annotation;

import java.lang.annotation.*;

/**
 * @author: iohw
 * @date: 2025/5/7 16:18
 * @description:
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MdcDot {
    String bizCode() default "";
}
