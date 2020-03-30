package com.cmsnesia.reactivewebclient.annotation;

import com.cmsnesia.reactivewebclient.http.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebfluxClient {

    String name();

    @AliasFor("path")
    String value() default "";

    @AliasFor("value")
    String path() default "";

    Method method() default Method.GET;

    String[] headers() default {};

}
