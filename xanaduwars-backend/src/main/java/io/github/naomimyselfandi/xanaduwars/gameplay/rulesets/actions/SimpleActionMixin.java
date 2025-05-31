package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.naomimyselfandi.xanaduwars.gameplay.Action;
import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;

/// A helper for implementing non-generic actions.
@ConvenienceMixin
public interface SimpleActionMixin<S extends Element, T> extends Action<S, T> {

    @Override
    default JavaType targetType(TypeFactory typeFactory) {
        return typeFactory.constructType(getClass()).findTypeParameters(Action.class)[1];
    }

}
