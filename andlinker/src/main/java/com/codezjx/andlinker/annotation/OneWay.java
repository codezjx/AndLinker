package com.codezjx.andlinker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicate a remote call does not block, it simply sends the transaction data and immediately
 * returns, same as "oneway" tag in AIDL.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface OneWay {
    
}
