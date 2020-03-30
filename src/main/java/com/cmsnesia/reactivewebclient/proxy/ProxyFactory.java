package com.cmsnesia.reactivewebclient.proxy;

import com.cmsnesia.reactivewebclient.proxy.invoker.ObjectInvoker;

public interface ProxyFactory {

    boolean canProxy(Class<?>... proxyClasses);

    <T> T createInvokerProxy(ObjectInvoker invoker, Class<?>... proxyClasses);

    <T> T createInvokerProxy(ClassLoader classLoader, ObjectInvoker invoker, Class<?>... proxyClasses);
}