package com.cmsnesia.reactivewebclient.http.client;

import com.cmsnesia.reactivewebclient.annotation.Request;
import com.cmsnesia.reactivewebclient.annotation.WebfluxClient;
import com.cmsnesia.reactivewebclient.metadata.*;
import com.cmsnesia.reactivewebclient.proxy.invoker.AbstractInvoker;
import com.cmsnesia.reactivewebclient.util.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpMethod;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.function.Consumer;

public class Webclient extends AbstractInvoker {

    private final String baseUrl;
    private final WebClient.Builder builder;

    public Webclient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.builder = WebClient
                .builder()
                .codecs(new Consumer<ClientCodecConfigurer>() {
                    @Override
                    public void accept(ClientCodecConfigurer clientCodecConfigurer) {
                        clientCodecConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder());
                        clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder());
                    }
                })
                .baseUrl(baseUrl);
    }

    @Override
    public Object invokeImpl(Object proxy, Method method, Object[] args) throws Throwable {
        Scheduler scheduler = Schedulers.boundedElastic();
        WebfluxClient webfluxClient = method.getDeclaringClass().getDeclaredAnnotation(WebfluxClient.class);
        Request requestMapping = method.getAnnotation(Request.class);
        Parameter[] parameters = method.getParameters();
        Publisher publisher = prepareRequest(webfluxClient, requestMapping, parameters, args, method.getReturnType());
        if (Flux.class.isAssignableFrom(method.getReturnType())) {
            return Flux.from(publisher).subscribeOn(scheduler);
        } else if (Mono.class.isAssignableFrom(method.getReturnType())) {
            return Mono.from(publisher).subscribeOn(scheduler);
        } else {
            throw new IllegalArgumentException("Return type " + method.getReturnType().getName() + " is not supported");
        }
    }

    private <T> Publisher<T> prepareRequest(WebfluxClient client, Request requestMapping, Parameter[] parameters, Object[] args, Class<T> returnType) {
        RequestPathMetadata pathMetadata = new RequestPathMetadata(client, requestMapping, parameters, args);
        RequestParamMetadata paramMetadata = new RequestParamMetadata(client, requestMapping, parameters, args);
        RequestMethodMetadata methodMetadata = new RequestMethodMetadata(client, requestMapping);
        RequestHeaderMetadata headerMetadata = new RequestHeaderMetadata(client, requestMapping, parameters, args);
        RequestBodyMetadata bodyMetadata = new RequestBodyMetadata(client, requestMapping, parameters, args);

        String path = pathMetadata.metadata();
        String query = paramMetadata.metadata();
        String uri = baseUrl + path + (StringUtils.isEmpty(query) ? "" : "?" + query);
        WebClient.RequestBodySpec requestSpec = builder
                .build()
                .method(HttpMethod.resolve(methodMetadata.metadata().name()))
                .uri(URI.create(uri));

        requestSpec.headers(httpHeaders -> {
            headerMetadata.metadata().forEach((name, values) -> {
                httpHeaders.addAll(name, values);
            });
        });
        if (bodyMetadata != null && bodyMetadata.metadata() != null) {
            requestSpec.bodyValue(bodyMetadata.metadata());
        }

        WebClient.ResponseSpec responseSpec = requestSpec.retrieve();

        if (Mono.class.isAssignableFrom(returnType)) {
            return responseSpec.bodyToMono(returnType);
        } else if (Flux.class.isAssignableFrom(returnType)) {
            return responseSpec.bodyToFlux(returnType);
        } else {
            throw new RuntimeException("");
        }
    }

}
