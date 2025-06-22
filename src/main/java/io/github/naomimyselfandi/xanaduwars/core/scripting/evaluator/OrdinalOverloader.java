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
            return doOperate(operation, l.ordinal(), toInt(rhs));
        } else if (lhs instanceof Integer l && rhs instanceof Ordinal<?> r) {
            return doOperate(operation, l, r.ordinal());
        } else {
            return delegate.operate(operation, lhs, rhs);
        }
    }

    private Object doOperate(Operation operation, int lhs, int rhs) {
        return switch (operation) {
            case ADD -> lhs + rhs;
            case SUBTRACT -> lhs - rhs;
            case MULTIPLY -> lhs * rhs;
            case DIVIDE -> lhs / rhs;
            case MODULUS -> lhs % rhs;
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
