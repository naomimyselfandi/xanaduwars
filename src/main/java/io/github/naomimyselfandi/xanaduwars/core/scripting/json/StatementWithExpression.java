package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

record StatementWithExpression(Expression expression) implements Statement {

    @Override
    public Object execute(EvaluationContext context, Class<?> type) {
        // Ignoring the type here is intentional - we're evaluating
        // the expression for side effects and ignoring the result.
        var _ = expression.getValue(context);
        return PROCEED;
    }

    @Override
    @JsonValue
    public String toString() {
        return expression.getExpressionString();
    }

}
