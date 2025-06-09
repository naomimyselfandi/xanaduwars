package io.github.naomimyselfandi.xanaduwars.core.scripting;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.expression.EvaluationContext;

record StatementWithContinue(int depth) implements Statement {

    @Override
    public void execute(EvaluationContext context) throws Break, Continue, Return {
        throw new Continue(depth);
    }

    @Override
    @JsonValue
    public String toString() {
        return "continue " + depth;
    }

}
