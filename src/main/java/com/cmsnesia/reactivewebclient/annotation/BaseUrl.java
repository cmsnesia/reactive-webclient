package com.cmsnesia.reactivewebclient.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BaseUrl {

    @AliasFor("baseUrl")
    String value();

    @AliasFor("value")
    String baseUrl() default "";

}
