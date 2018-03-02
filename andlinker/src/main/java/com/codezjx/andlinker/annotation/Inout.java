package com.codezjx.andlinker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Directional tag indicating which way the data goes, same as "inout" tag in AIDL.
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Inout {
    
}
