package com.codezjx.andlinker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicate a parameter is a remote callback type.
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Callback {

}
