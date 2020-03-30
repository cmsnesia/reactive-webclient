package com.cmsnesia.reactivewebclient.interceptor;

import java.lang.reflect.Method;

public interface Invocation {

    Object[] getArguments();

    Method getMethod();

    Object getProxy();

    Object proceed() throws Throwable;

}
