package io.github.naomimyselfandi.xanaduwars.core.scripting;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

record StatementWithExpression(Expression expression) implements Statement {

    @Override
    public void execute(EvaluationContext context) throws Break, Continue, Return {
        var _ = expression.getValue(context);
    }

    @Override
    @JsonValue
    public String toString() {
        return expression.getExpressionString();
    }

}
