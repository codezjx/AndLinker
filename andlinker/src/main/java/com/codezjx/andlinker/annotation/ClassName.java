package com.codezjx.andlinker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specify the remote service interface name, usually a full class name, eg: com.your.package.InterfaceName.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface ClassName {
    String value();
}
