package com.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: shiguang
 * @Date: 2021/8/14
 * @Description:
 **/
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD,ElementType.TYPE,ElementType.METHOD})
public @interface BoundInfo {
    String value() default "bound";
}
