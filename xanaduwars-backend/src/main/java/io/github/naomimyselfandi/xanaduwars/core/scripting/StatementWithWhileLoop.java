package io.github.naomimyselfandi.xanaduwars.core.scripting;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.util.List;
import java.util.stream.Collectors;

record StatementWithWhileLoop(Expression condition, List<Statement> body) implements Statement {

    StatementWithWhileLoop(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = List.copyOf(body);
    }

    @Override
    public void execute(EvaluationContext context) throws Break, Continue, Return {
        outer_loop:
        while (Boolean.TRUE.equals(condition.getValue(context, Boolean.class))) {
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
        return "while %s%s; done".formatted(condition.getExpressionString(), bodyString);
    }

}
