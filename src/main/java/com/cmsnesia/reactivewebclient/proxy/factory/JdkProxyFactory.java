package com.cmsnesia.reactivewebclient.proxy.factory;

import com.cmsnesia.reactivewebclient.proxy.invoker.ObjectInvoker;
import com.cmsnesia.reactivewebclient.proxy.ProxyFactory;
import com.cmsnesia.reactivewebclient.util.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkProxyFactory implements ProxyFactory {

    @Override
    public boolean canProxy(Class<?>... proxyClasses) {
        if (proxyClasses == null) {
            throw new RuntimeException("Only support single proxy.");
        }
        for (Class<?> proxyClass : proxyClasses) {
            if (!proxyClass.isInterface()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public <T> T createInvokerProxy(ObjectInvoker invoker, Class<?>... proxyClasses) {
        return createInvokerProxy(Thread.currentThread().getContextClassLoader(), invoker, proxyClasses);
    }

    @Override
    public <T> T createInvokerProxy(ClassLoader classLoader, ObjectInvoker invoker, Class<?>... proxyClasses) {
        @SuppressWarnings("unchecked")
        T result = (T) Proxy.newProxyInstance(classLoader, proxyClasses, new InvokerInvocationHandler(invoker));
        return result;
    }

    private static abstract class AbstractInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (ProxyUtil.isHashCode(method)) {
                return Integer.valueOf(System.identityHashCode(proxy));
            }

            if (ProxyUtil.isEqualsMethod(method)) {
                return Boolean.valueOf(proxy == args[0]);
            }

            return invokeImpl(proxy, method, args);
        }

        protected abstract Object invokeImpl(Object proxy, Method method, Object[] args) throws Throwable;
    }

    private static class InvokerInvocationHandler extends AbstractInvocationHandler {

        private final ObjectInvoker invoker;

        public InvokerInvocationHandler(ObjectInvoker invoker) {
            this.invoker = invoker;
        }

        @Override
        public Object invokeImpl(Object proxy, Method method, Object[] args) throws Throwable {
            return invoker.invoke(proxy, method, args);
        }
    }

}
