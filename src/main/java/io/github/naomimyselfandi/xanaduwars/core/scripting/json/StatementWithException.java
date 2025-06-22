package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.core.scripting.ScriptingException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

record StatementWithException(Expression expression) implements Statement {

    @Override
    public Object execute(EvaluationContext context, Class<?> type) {
        throw new ScriptingException(String.valueOf(expression.getValue(context)));
    }

    @Override
    @JsonValue
    public String toString() {
        return "throw %s".formatted(expression.getExpressionString());
    }

}
