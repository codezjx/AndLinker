package com.codezjx.aidllib.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by codezjx on 2017/9/14.<br/>
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface ClassName {
    String value();
}
