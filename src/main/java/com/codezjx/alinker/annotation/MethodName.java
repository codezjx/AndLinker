package com.codezjx.alinker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by codezjx on 2017/9/14.<br/>
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface MethodName {
    String value();
}
