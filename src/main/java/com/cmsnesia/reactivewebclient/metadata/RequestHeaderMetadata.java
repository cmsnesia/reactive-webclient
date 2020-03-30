package com.cmsnesia.reactivewebclient.metadata;

import com.cmsnesia.reactivewebclient.annotation.Header;
import com.cmsnesia.reactivewebclient.annotation.Request;
import com.cmsnesia.reactivewebclient.annotation.WebfluxClient;
import com.cmsnesia.reactivewebclient.util.StringUtils;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaderMetadata extends Metadata<Map<String, List<String>>> {

    public RequestHeaderMetadata(WebfluxClient webfluxClient, Request requestMapping, Parameter[] parameters, Object[] args) {
        super(webfluxClient, requestMapping, parameters, args);
    }

    @Override
    public Map<String, List<String>> metadata() {
        Map<String, List<String>> finalHeaders = new HashMap<>();

        int headerCount = (client != null ? client.headers().length : 0)
                + (requestMapping.headers() != null ? requestMapping.headers().length : 0);

        String[] headers = new String[headerCount];

        int counter = 0;
        if (client.headers() != null) {
            for (String header : client.headers()) {
                headers[counter++] = header;
            }
        }
        if (requestMapping.headers() != null) {
            for (String header : requestMapping.headers()) {
                headers[counter++] = header;
            }
        }

        for (int i = 0; i < headers.length; i++) {
            String[] splited = headers[i].split("=");
            if (splited != null && splited.length > 1) {
                String name = splited[0].trim();
                List<String> values = new ArrayList<>();
                for (int j = 1; j < splited.length; j++) {
                    values.add(splited[j].trim());
                }
                finalHeaders.put(name, values);
            }
        }

        for (int i = 0; i < parameters.length; i++) {
            Header requestHeader = parameters[i].getAnnotation(Header.class);
            if (requestHeader != null) {
                Parameter parameter = parameters[i];
                String headerName = !StringUtils.isEmpty(requestHeader.name()) ? requestHeader.name() : requestHeader.value();
                if (StringUtils.isEmpty(headerName)) {
                    headerName = parameter.getName();
                }
                String headerValue = args[i] == null ? "" : args[i].toString();
                if (requestHeader.required() && StringUtils.isEmpty(headerValue)) {
                    throw new RuntimeException("Request header (" + headerName + ") is required!");
                }
                List<String> values = new ArrayList<>();
                if (headerValue.contains(",")) {
                    String[] splited = headerValue.split(",");
                    for (String val : splited) {
                        values.add(val.trim());
                    }
                }
                finalHeaders.put(headerName, values);
            }
        }

        return finalHeaders;
    }
}
