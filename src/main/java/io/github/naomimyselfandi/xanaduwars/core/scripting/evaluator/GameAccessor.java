package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

record GameAccessor(TypedValue game) implements PropertyAccessor {

    @Override
    public boolean canRead(EvaluationContext context, @Nullable Object target, String name) {
        return target instanceof Query<?> && name.equals("game");
    }

    @Override
    public TypedValue read(EvaluationContext context, @Nullable Object target, String name) {
        return game;
    }

    @Override
    public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) {
        return false;
    }

    @Override
    public void write(
            EvaluationContext context,
            @Nullable Object target,
            String name,
            @Nullable Object newValue
    ) throws AccessException {
        throw new AccessException("Cannot overwrite game.");
    }

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[]{Query.class};
    }

}
