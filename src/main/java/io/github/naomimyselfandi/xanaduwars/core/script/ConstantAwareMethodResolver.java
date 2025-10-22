package io.github.naomimyselfandi.xanaduwars.core.script;

import org.springframework.expression.spel.support.ReflectiveMethodResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/// A method resolver that is aware of our script constant marker interface.
final class ConstantAwareMethodResolver extends ReflectiveMethodResolver {

    @Override
    protected boolean isCandidateForInvocation(Method method, Class<?> targetClass) {
        if (ScriptConstant.class.isAssignableFrom(targetClass)) {
            // Scripts can't call mutator methods of scripting constants.
            if (method.getName().startsWith("set") || method.getReturnType() == void.class) {
                return false;
            }
        }
        return !(Modifier.isStatic(method.getModifiers()));
    }

}
