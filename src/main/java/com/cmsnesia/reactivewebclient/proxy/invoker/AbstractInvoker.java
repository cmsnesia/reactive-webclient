package com.cmsnesia.reactivewebclient.proxy.invoker;

import com.cmsnesia.reactivewebclient.util.ProxyUtil;

import java.lang.reflect.Method;

public abstract class AbstractInvoker implements ObjectInvoker {

    @Override
    public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
        if (ProxyUtil.isHashCode(method)) {
            return Integer.valueOf(System.identityHashCode(proxy));
        }

        if (ProxyUtil.isEqualsMethod(method)) {
            return Boolean.valueOf(proxy == args[0]);
        }

        if (ProxyUtil.isToStringsMethod(method)) {
            return this.toString();
        }

        return invokeImpl(proxy, method, args);
    }

    public abstract Object invokeImpl(Object proxy, Method method, Object[] args) throws Throwable;
}