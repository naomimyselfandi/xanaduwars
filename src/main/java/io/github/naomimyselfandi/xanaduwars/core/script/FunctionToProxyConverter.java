package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/// A SpEl converter that creates proxies from scripting functions. Any
/// functional interface type can be proxied.
final class FunctionToProxyConverter implements ConditionalGenericConverter {

    private static final ClassLoader CLASS_LOADER = FunctionToProxyConverter.class.getClassLoader();

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return Function.class.isAssignableFrom(sourceType.getType()) && isFunctionalInterface(targetType.getType());
    }

    @Override
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        var function = (Function) Objects.requireNonNull(source);
        var interfaces = new Class<?>[]{targetType.getType()};
        return Proxy.newProxyInstance(CLASS_LOADER, interfaces, new FunctionInvocationHandler(function));
    }

    @Override
    public @Nullable Set<ConvertiblePair> getConvertibleTypes() {
        return null; // indicates that matches() should be used alone
    }

    private static boolean isFunctionalInterface(Class<?> type) {
        return type.isInterface() && type != Function.class && Arrays
                .stream(type.getMethods())
                .filter(method -> !ReflectionUtils.isObjectMethod(method))
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .filter(method -> !method.isDefault())
                .count() == 1;
    }

}
