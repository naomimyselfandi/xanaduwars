package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.expression.EvaluationContext;

record StatementWithBreak(int depth) implements Statement {

    @Override
    public Object execute(EvaluationContext context, Class<?> type) {
        return new Break(depth);
    }

    @Override
    @JsonValue
    public String toString() {
        return "break %d".formatted(depth);
    }

}
