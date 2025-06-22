package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

record StatementWithForLoop(String variable, Expression source, Statement body) implements Statement {

    @Override
    @SuppressWarnings("DeconstructionCanBeUsed")
    public @Nullable Object execute(EvaluationContext context, Class<?> type) {
        var iterable = iterable(context);
        for (var value : iterable) {
            context.setVariable(variable, value);
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

    private Iterable<?> iterable(EvaluationContext context) {
        return switch (source.getValue(context)) {
            case Iterable<?> iterable -> iterable;
            case Map<?, ?> map -> map.entrySet();
            case Stream<?> stream -> stream.<Object>map(Function.identity())::iterator;
            case null -> List.of();
            case Object o -> throw new ClassCastException("`%s` evaluated to non-iterable %s.".formatted(source, o));
        };
    }

    @Override
    @JsonValue
    public String toString() {
        return "for %s in %s:\n%s".formatted(variable, source.getExpressionString(), ScriptImpl.indent(body));
    }

}
