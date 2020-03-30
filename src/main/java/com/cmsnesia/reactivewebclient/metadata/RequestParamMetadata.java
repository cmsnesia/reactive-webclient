package com.cmsnesia.reactivewebclient.metadata;

import com.cmsnesia.reactivewebclient.annotation.Request;
import com.cmsnesia.reactivewebclient.annotation.QueryParam;
import com.cmsnesia.reactivewebclient.annotation.WebfluxClient;
import com.cmsnesia.reactivewebclient.util.StringUtils;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class RequestParamMetadata extends Metadata<String> {

    public RequestParamMetadata(WebfluxClient webfluxClient, Request requestMapping, Parameter[] parameters, Object[] args) {
        super(webfluxClient, requestMapping, parameters, args);
    }

    @Override
    public String metadata() {
        List<String> query = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            QueryParam requestParam = parameters[i].getAnnotation(QueryParam.class);
            if (requestParam != null) {
                Parameter parameter = parameters[i];
                String paramName = !StringUtils.isEmpty(requestParam.name()) ? requestParam.name() : requestParam.value();
                if (StringUtils.isEmpty(paramName)) {
                    paramName = parameter.getName();
                }
                String paramValue = args[i] == null ? "" : args[i].toString();
                if (requestParam.required() && StringUtils.isEmpty(paramValue)) {
                    throw new RuntimeException("Request param (" + paramName + ") is required!");
                }
                query.add(paramName + "=" + (paramValue == null ? "" : paramValue));
            }
        }
        return String.join("&", query);
    }
}
