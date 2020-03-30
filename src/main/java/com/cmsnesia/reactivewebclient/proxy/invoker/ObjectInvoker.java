package com.cmsnesia.reactivewebclient.proxy.invoker;


import java.lang.reflect.Method;

public interface ObjectInvoker {

    Object invoke(Object proxy, Method method, Object... args) throws Throwable;

}