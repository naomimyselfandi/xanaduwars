package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.naomimyselfandi.xanaduwars.gameplay.Action;
import io.github.naomimyselfandi.xanaduwars.gameplay.Actor;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/// A helper for implementing actions that target enum constants.
@ConvenienceMixin
public interface ActionWithEnumTarget<S extends Actor, T extends Enum<T>> extends SimpleActionMixin<S, T> {

    @Override
    default Stream<T> enumerateTargets(GameState gameState) {
        interface Holder {
            Map<Class<?>, List<?>> CACHE = new ConcurrentHashMap<>();
            Function<Class<?>, List<?>> GETTER = actionType -> {
                var enumConstants = TypeFactory
                        .defaultInstance()
                        .constructType(actionType)
                        .findTypeParameters(Action.class)[1]
                        .getRawClass()
                        .getEnumConstants();
                return List.of(enumConstants);
            };
        }
        @SuppressWarnings("unchecked")
        var result = (Stream<T>) Holder.CACHE.computeIfAbsent(getClass(), Holder.GETTER).stream();
        return result;
    }

}
