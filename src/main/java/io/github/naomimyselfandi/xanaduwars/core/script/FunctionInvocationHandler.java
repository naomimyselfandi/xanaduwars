package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/// An invocation handler used to create proxies from script functions.
record FunctionInvocationHandler(Function function) implements InvocationHandler {

    @Override
    public @Nullable Object invoke(Object proxy, Method method, @Nullable Object @Nullable [] args) throws Throwable {
        if (ReflectionUtils.isEqualsMethod(method)) {
            return proxy == Objects.requireNonNull(args)[0];
        } else if (ReflectionUtils.isHashCodeMethod(method)) {
            return System.identityHashCode(proxy);
        } else if (ReflectionUtils.isToStringMethod(method)) {
            return Objects.toIdentityString(proxy);
        } else if (method.isDefault()) {
            return InvocationHandler.invokeDefault(proxy, method, args);
        } else if (args != null) {
            return function.call(args);
        } else {
            return function.call();
        }
    }

}
