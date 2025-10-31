package io.github.naomimyselfandi.xanaduwars.core.script;

import org.springframework.expression.spel.support.ReflectiveMethodResolver;

import java.lang.reflect.Method;

/// A method resolver that is aware of our script constant marker interface.
final class ConstantAwareMethodResolver extends ReflectiveMethodResolver {

    @Override
    protected boolean isCandidateForInvocation(Method method, Class<?> targetClass) {
        return !(ScriptConstant.class.isAssignableFrom(targetClass) && isMutatorMethod(method));
    }

    private static boolean isMutatorMethod(Method method) {
        return method.getName().startsWith("set") || method.getReturnType() == void.class;
    }

}
