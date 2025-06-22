package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

record StatementWithWhileLoop(Expression condition, Statement body) implements Statement {

    @Override
    @SuppressWarnings("DeconstructionCanBeUsed")
    public @Nullable Object execute(EvaluationContext context, Class<?> type) {
        while (Boolean.TRUE.equals(condition.getValue(context, Boolean.class))) {
            var result = body.execute(context, type);
            if (result instanceof Break b) {
                if (b.ordinal() == 1) {
                    break;
                } else {
                    return b.minus(1);
                }
            }
            if (result instanceof Continue c) {
                if (c.ordinal() == 1) {
                    continue;
                } else {
                    return c.minus(1);
                }
            }
            if (!PROCEED.equals(result)) {
                return result;
            }
        }
        return PROCEED;
    }

    @Override
    @JsonValue
    public String toString() {
        return "while %s:\n%s".formatted(condition.getExpressionString(), ScriptImpl.indent(body));
    }

}
