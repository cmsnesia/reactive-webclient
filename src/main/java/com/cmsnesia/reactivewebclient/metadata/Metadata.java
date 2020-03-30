package com.cmsnesia.reactivewebclient.metadata;

import com.cmsnesia.reactivewebclient.annotation.Request;
import com.cmsnesia.reactivewebclient.annotation.WebfluxClient;

import java.lang.reflect.Parameter;

abstract class Metadata<T> {

    protected final WebfluxClient client;
    protected final Request requestMapping;
    protected final Parameter[] parameters;
    protected final Object[] args;

    Metadata(WebfluxClient webfluxClient, Request requestMapping, Parameter[] parameters, Object[] args) {
        this.client = webfluxClient;
        this.requestMapping = requestMapping;
        if (parameters.length != args.length) {
            throw new RuntimeException("");
        }
        this.parameters = parameters;
        this.args = args;
    }

    public abstract T metadata();

}
