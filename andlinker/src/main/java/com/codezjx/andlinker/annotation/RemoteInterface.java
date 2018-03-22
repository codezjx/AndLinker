package com.codezjx.andlinker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specify the interface as remote service interface.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface RemoteInterface {
    
}
