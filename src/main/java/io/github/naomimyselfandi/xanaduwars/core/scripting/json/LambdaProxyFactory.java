package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

@UtilityClass
class LambdaProxyFactory {

    private static final ClassLoader CLASS_LOADER = LambdaProxyFactory.class.getClassLoader();

    Object create(String name, Script script, EvaluationContext context, List<String> parameters, Class<?> signature) {
        var sig = new Class<?>[]{signature};
        return Proxy.newProxyInstance(CLASS_LOADER, sig, (proxy, method, arguments) -> {
            if (method.isDefault()) {
                return InvocationHandler.invokeDefault(proxy, method, arguments);
            } else return switch (method.getName()) {
                case "equals" -> proxy == arguments[0];
                case "hashCode" -> System.identityHashCode(proxy);
                case "toString" -> "%s:%s:%d".formatted(name, signature.getSimpleName(), proxy.hashCode());
                default -> script.run(new LambdaContext(context, parameters, copy(arguments)), method.getReturnType());
            };
        });
    }

    private static List<Object> copy(Object @Nullable [] arguments) {
        return arguments == null ? List.of() : List.of(arguments);
    }

}
