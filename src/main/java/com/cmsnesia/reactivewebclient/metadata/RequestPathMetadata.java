package com.cmsnesia.reactivewebclient.metadata;

import com.cmsnesia.reactivewebclient.annotation.PathVariable;
import com.cmsnesia.reactivewebclient.annotation.Request;
import com.cmsnesia.reactivewebclient.annotation.WebfluxClient;
import com.cmsnesia.reactivewebclient.util.StringUtils;

import java.lang.reflect.Parameter;

public class RequestPathMetadata extends Metadata<String> {

    public RequestPathMetadata(WebfluxClient webfluxClient, Request requestMapping, Parameter[] parameters, Object[] args) {
        super(webfluxClient, requestMapping, parameters, args);
    }

    @Override
    public String metadata() {
        String defaultPath = StringUtils.isEmpty(client.path()) ? client.path() : client.value();
        String requestPath = StringUtils.isEmpty(requestMapping.path()) ? requestMapping.value() : requestMapping.path();

        defaultPath = clean(defaultPath);
        requestPath = clean(requestPath);
        String path = "";

        if (!StringUtils.isEmpty(defaultPath)) {
            path += "/" + defaultPath;
        }
        if (!StringUtils.isEmpty(requestPath)) {
            path += "" + requestPath;
        }

        for (int i = 0; i < parameters.length; i++) {
            PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                Parameter parameter = parameters[i];
                String pathId = !StringUtils.isEmpty(pathVariable.name()) ? pathVariable.name() : pathVariable.value();
                if (StringUtils.isEmpty(pathId)) {
                    pathId = parameter.getName();
                }
                String pathValue = args[i] == null ? "" : args[i].toString();
                if (pathVariable.required() && StringUtils.isEmpty(pathValue)) {
                    throw new RuntimeException("Path variable (" + pathId + ") is required!");
                }
                path = path.replace("{" + pathId + "}", pathValue);
            }
        }
        return path;
    }

    private String clean(String str) {
        char[] chars = str.toCharArray();
        int length = chars.length;
        int start = 0;
        int end = chars.length;
        for (int i = 0; i < length; i++) {
            if (chars[i] == '/' && chars[i + 1] == '/') {
                start = i + 1;
            } else {
                break;
            }
        }
        for (int j = length - 1; j >= 0; j--) {
            if (chars[j] == '/' && chars[j - 1] == '/') {
                end = j - 1;
            } else {
                break;
            }
        }
        return str.substring(start, end);
    }
}
