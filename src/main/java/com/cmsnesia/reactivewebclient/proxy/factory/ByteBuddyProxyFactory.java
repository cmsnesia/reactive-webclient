package com.cmsnesia.reactivewebclient.proxy.factory;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

import com.cmsnesia.reactivewebclient.proxy.invoker.ObjectInvoker;
import com.cmsnesia.reactivewebclient.proxy.ProxyFactory;
import com.cmsnesia.reactivewebclient.util.ProxyUtil;
import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ByteBuddyProxyFactory implements ProxyFactory {

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

    @SneakyThrows
    @Override
    public <T> T createInvokerProxy(ClassLoader classLoader, ObjectInvoker invoker, Class<?>... proxyClasses) {
        Class<?> proxyType = new ByteBuddy()
                .subclass(Object.class)
                .implement(proxyClasses[0])
                .method(isDeclaredBy(proxyClasses[0]))
                .intercept(InvocationHandlerAdapter.of(new InvokerInvocationHandler(invoker)))
                .make()
                .load(classLoader, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        return (T) proxyType.newInstance();
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
