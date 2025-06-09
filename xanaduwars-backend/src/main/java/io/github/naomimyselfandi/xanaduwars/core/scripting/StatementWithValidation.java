package io.github.naomimyselfandi.xanaduwars.core.scripting;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

record StatementWithValidation(Expression condition) implements Statement {

    private static final Return FALSE = new Return(false);

    @Override
    public void execute(EvaluationContext context) throws Break, Continue, Return {
        if (!Boolean.TRUE.equals(condition.getValue(context, Boolean.class))) {
            throw FALSE;
        }
    }

    @Override
    @JsonValue
    public String toString() {
        return "validate " + condition.getExpressionString();
    }

}
