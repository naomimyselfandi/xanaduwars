package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.util.Ordinal;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.*;

import java.util.Objects;

record OrdinalOverloader(OperatorOverloader delegate) implements OperatorOverloader {

    @Override
    public boolean overridesOperation(Operation operation, @Nullable Object lhs, @Nullable Object rhs)
            throws EvaluationException {
        return (isIntOrOrdinal(lhs, rhs)) || delegate.overridesOperation(operation, lhs, rhs);
    }

    @Override
    public Object operate(Operation operation, @Nullable Object lhs, @Nullable Object rhs)
            throws EvaluationException {
        if (lhs instanceof Ordinal<?> l && isIntOrOrdinal(rhs)) {
            return operate(operation, l.ordinal(), toInt(rhs), l);
        } else if (lhs instanceof Integer l && rhs instanceof Ordinal<?> r) {
            return operate(operation, l, r.ordinal(), r);
        } else {
            return delegate.operate(operation, lhs, rhs);
        }
    }

    private Object operate(Operation operation, int lhs, int rhs, Ordinal<?> ordinal) {
        return switch (operation) {
            case ADD -> ordinal.withOrdinal(lhs + rhs);
            case SUBTRACT -> ordinal.withOrdinal(lhs - rhs);
            case MULTIPLY -> ordinal.withOrdinal(lhs * rhs);
            case DIVIDE -> ordinal.withOrdinal(lhs / rhs);
            case MODULUS -> ordinal.withOrdinal(lhs % rhs);
            case POWER -> Math.pow(lhs, rhs);
        };
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
