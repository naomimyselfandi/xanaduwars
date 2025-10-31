package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

import java.util.Objects;

final class LibraryAccessor implements PropertyAccessor {

    private static final Class<?>[] TARGET = new Class<?>[]{Library.class};

    @Override
    public boolean canRead(EvaluationContext context, @Nullable Object target, String name) {
        return (target instanceof Library library) && library.lookup(name) != null;
    }

    @Override
    public TypedValue read(EvaluationContext context, @Nullable Object target, String name) {
        return new TypedValue(((Library) Objects.requireNonNull(target)).lookup(name));
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
        throw new AccessException("Not supported");
    }

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return TARGET;
    }

}
