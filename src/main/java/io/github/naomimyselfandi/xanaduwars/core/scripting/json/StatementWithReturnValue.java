package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

record StatementWithReturnValue(Expression expression) implements Statement {

    @Override
    public @Nullable Object execute(EvaluationContext context, Class<?> type) {
        return expression.getValue(context, type);
    }

    @Override
    @JsonValue
    public String toString() {
        return "return %s".formatted(expression.getExpressionString());
    }

}
