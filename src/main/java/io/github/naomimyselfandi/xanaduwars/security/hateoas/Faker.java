package io.github.naomimyselfandi.xanaduwars.security.hateoas;

import lombok.experimental.UtilityClass;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.*;

/// A utility class for creating placeholders. [SecurityAwareAssembler], and
/// Spring's own `methodOn/`linkTo` methods, use actual method calls to refer
/// to controller endpoints. When a method parameter isn't used in the link or
/// permission checks, a placeholder can be used rather than constructing an
/// instance manually.
@UtilityClass
public class Faker {

    /// Create a placeholder. This method is typically statically imported, and
    /// its unusual name is chosen with this in mind. It automatically infers a
    /// return type; `$()` automatically creates an appropriately typed object,
    /// without needing any manual specification.
    ///
    /// [SecurityAwareAssembler], and Spring's own `methodOn`/`linkTo` methods,
    /// use actual method calls to refer to controller endpoints. When a method
    /// parameter isn't used in the link or permission checks, a placeholder can
    /// be used rather than constructing an instance manually.
    ///
    /// @param typeHint Do not pass any arguments to this method. This parameter
    /// only exists to support the type inference above.
    @SafeVarargs
    public static <F> F $(F... typeHint) {
        @SuppressWarnings("unchecked")
        var fake = (F) createPlaceholder(typeHint.getClass().getComponentType());
        return fake;
    }

    private static final Map<Class<?>, Object> FAKES = Map.ofEntries(
            Map.entry(Object.class, new Object()),
            Map.entry(List.class, List.of()),
            Map.entry(Collection.class, List.of()),
            Map.entry(Iterable.class, List.of()),
            Map.entry(Set.class, Set.of()),
            Map.entry(Map.class, Map.of()),
            Map.entry(Optional.class, Optional.empty()),
            Map.entry(OptionalInt.class, OptionalInt.empty()),
            Map.entry(OptionalDouble.class, OptionalDouble.empty()),
            Map.entry(OptionalLong.class, OptionalLong.empty()),
            Map.entry(String.class, ""),
            Map.entry(Boolean.class, false),
            Map.entry(boolean.class, false),
            Map.entry(Integer.class, 0),
            Map.entry(int.class, 0),
            Map.entry(Double.class, 0.0),
            Map.entry(double.class, 0.0),
            Map.entry(Long.class, 0L),
            Map.entry(long.class, 0L)
    );

    private static final MethodInterceptor INTERCEPTOR = invocation -> {
        var method = invocation.getMethod();
        return ReflectionUtils.isToStringMethod(method) ? "<<placeholder>>" : createPlaceholder(method.getReturnType());
    };

    @SuppressWarnings("DataFlowIssue")
    private static Object createPlaceholder(Class<?> type) {
        if (FAKES.get(type) instanceof Object fake) {
            return fake;
        } else if (type.isEnum()) {
            return type.getEnumConstants()[0];
        } else if (type.isSealed()) {
            return createPlaceholder(type.getPermittedSubclasses()[0]);
        } else if (Modifier.isFinal(type.getModifiers())) {
            return null; // probably fine...
        } else {
            var proxyFactory = new ProxyFactory();
            proxyFactory.addAdvice(INTERCEPTOR);
            if (type.isInterface()) {
                proxyFactory.addInterface(type);
            } else {
                proxyFactory.setOptimize(true);
                proxyFactory.setTargetClass(type);
                proxyFactory.setProxyTargetClass(true);
            }
            return proxyFactory.getProxy();
        }
    }

}
