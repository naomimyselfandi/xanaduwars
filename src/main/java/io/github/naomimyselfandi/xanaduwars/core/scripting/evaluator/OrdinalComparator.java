package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.util.Ordinal;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.*;

import java.util.Objects;

record OrdinalComparator(TypeComparator delegate) implements TypeComparator {

    @Override
    public boolean canCompare(@Nullable Object lhs, @Nullable Object rhs) {
        return delegate.canCompare(lhs, rhs) || (isIntOrOrdinal(lhs, rhs));
    }

    @Override
    public int compare(@Nullable Object lhs, @Nullable Object rhs) throws EvaluationException {
        if (isIntOrOrdinal(lhs, rhs)) {
            return Integer.compare(toInt(lhs), toInt(rhs));
        } else {
            return delegate.compare(lhs, rhs);
        }
    }

    private boolean isIntOrOrdinal(@Nullable Object lhs, @Nullable Object rhs) {
        return isIntOrOrdinal(lhs) && isIntOrOrdinal(rhs);
    }

    private boolean isIntOrOrdinal(@Nullable Object object) {
        return object instanceof Integer || object instanceof Ordinal<?>;
    }

    private int toInt(@Nullable Object object) {
        return object instanceof Ordinal<?> it ? it.ordinal() : Objects.requireNonNull((Integer) object);
    }

}
