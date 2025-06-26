package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import lombok.experimental.Delegate;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;

record CustomPropertyAccessor(@Delegate(excludes = Excludes.class) PropertyAccessor delegate)
        implements PropertyAccessor {

    private interface Excludes {
        @SuppressWarnings("unused")
        TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException;
    }

    @Override
    public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        return CustomMethodResolver.unwrap(delegate.read(context, target, name));
    }

}
