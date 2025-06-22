package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

record StatementWithCondition(Expression condition, Statement yes, Statement no) implements Statement {

    @Override
    public @Nullable Object execute(EvaluationContext context, Class<?> type) {
        return (Boolean.TRUE.equals(condition.getValue(context, Boolean.class)) ? yes : no).execute(context, type);
    }

    @Override
    @JsonValue
    public String toString() {
        var c = condition.getExpressionString();
        var y = ScriptImpl.indent(yes);
        var n = ScriptImpl.indent(no);
        return "if %s:\n%s\nelse:\n%s".formatted(c, y, n);
    }

}
