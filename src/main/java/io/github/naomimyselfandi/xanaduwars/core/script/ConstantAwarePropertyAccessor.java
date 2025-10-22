package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;

import java.lang.reflect.Method;

/// A property accessor that is aware of our script constant marker interface.
final class ConstantAwarePropertyAccessor extends ReflectivePropertyAccessor {

    @Override
    protected @Nullable Method findSetterForProperty(String propertyName, Class<?> clazz, boolean mustBeStatic) {
        if (ScriptConstant.class.isAssignableFrom(clazz)) {
            // Scripts can't set properties of scripting constants.
            return null;
        } else {
            return super.findSetterForProperty(propertyName, clazz, mustBeStatic);
        }
    }

}
