package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

record StatementWithValidation(Expression condition, Expression message) implements Statement {

    @Override
    public @Nullable Object execute(EvaluationContext context, Class<?> type) {
        if (Boolean.TRUE.equals(condition.getValue(context, Boolean.class))) {
            return PROCEED;
        } else {
            return message.getValue(context, type);
        }
    }

    @Override
    @JsonValue
    public String toString() {
        return "validate %s :: %s".formatted(condition.getExpressionString(), message.getExpressionString());
    }

}
