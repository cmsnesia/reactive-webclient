package com.cmsnesia.reactivewebclient.metadata;

import com.cmsnesia.reactivewebclient.annotation.Request;
import com.cmsnesia.reactivewebclient.annotation.WebfluxClient;
import com.cmsnesia.reactivewebclient.http.Method;

import java.lang.reflect.Parameter;

public class RequestMethodMetadata extends Metadata<Method> {

    public RequestMethodMetadata(WebfluxClient webfluxClient, Request requestMapping) {
        super(webfluxClient, requestMapping, new Parameter[]{}, new Object[]{});
    }

    @Override
    public Method metadata() {
        Method requestMethod = client.method();
        return Method.resolve(requestMethod.name());
    }
}
