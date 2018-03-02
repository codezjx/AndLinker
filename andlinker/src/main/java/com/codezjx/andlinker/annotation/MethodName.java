package com.codezjx.andlinker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specify the remote service method name, usually use method name directly.
 * Warning: method name value don't support overloading yet.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface MethodName {
    String value();
}
