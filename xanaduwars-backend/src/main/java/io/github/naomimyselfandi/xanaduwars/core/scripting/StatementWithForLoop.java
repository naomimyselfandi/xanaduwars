package io.github.naomimyselfandi.xanaduwars.core.scripting;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

record StatementWithForLoop(String variable, Expression source, List<Statement> body) implements Statement {

    StatementWithForLoop(String variable, Expression source, List<Statement> body) {
        this.variable = variable;
        this.source = source;
        this.body = List.copyOf(body);
    }

    @Override
    public void execute(EvaluationContext context) throws Break, Continue, Return {
        var iterable = iterable(context);
        outer_loop:
        for (var value : iterable) {
            context.setVariable(variable, value);
            for (var statement : body) {
                try {
                    statement.execute(context);
                } catch (Break e) {
                    if (--e.depth > 0) throw e;
                    break outer_loop;
                } catch (Continue e) {
                    if (--e.depth > 0) throw e;
                    continue outer_loop;
                }
            }
        }
    }

    @Override
    @JsonValue
    public String toString() {
        var bodyString = body.stream().map("; %s"::formatted).collect(Collectors.joining());
        return "for %s : %s%s; done".formatted(variable, source.getExpressionString(), bodyString);
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

}
