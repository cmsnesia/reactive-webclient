package com.cmsnesia.reactivewebclient.metadata;

import com.cmsnesia.reactivewebclient.annotation.Body;
import com.cmsnesia.reactivewebclient.annotation.Request;
import com.cmsnesia.reactivewebclient.annotation.WebfluxClient;

import java.lang.reflect.Parameter;

public class RequestBodyMetadata extends Metadata<Object> {

    public RequestBodyMetadata(WebfluxClient webfluxClient, Request requestMapping, Parameter[] parameters, Object[] args) {
        super(webfluxClient, requestMapping, parameters, args);
    }

    @Override
    public Object metadata() {
        Object value = null;
        for (int i = 0; i < parameters.length; i++) {
            Body requestBody = parameters[i].getAnnotation(Body.class);
            if (requestBody != null) {
                if (value == null) {
                    value = args[i];
                } else {
                    throw new RuntimeException("");
                }
            }
        }
        return value;
    }
}
