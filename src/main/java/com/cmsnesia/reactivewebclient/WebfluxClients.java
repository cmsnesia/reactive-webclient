package com.cmsnesia.reactivewebclient;

import com.cmsnesia.reactivewebclient.proxy.factory.ByteBuddyProxyFactory;
import com.cmsnesia.reactivewebclient.proxy.ProxyFactory;
import com.cmsnesia.reactivewebclient.http.client.Webclient;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebfluxClients {

    public static Builder builder() {
        return new Builder();
    }

    @AllArgsConstructor
    public static class Builder {

        public <T> T target(Class<T> type, String baseUrl) {
            ProxyFactory proxyCreator = new ByteBuddyProxyFactory();
            return proxyCreator.createInvokerProxy(new Webclient(baseUrl), type);
        }
    }

}
