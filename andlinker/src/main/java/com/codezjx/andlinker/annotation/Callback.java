package com.codezjx.andlinker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by codezjx on 2017/9/14.<br/>
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Callback {

}
