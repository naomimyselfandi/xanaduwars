package io.github.naomimyselfandi.xanaduwars.core.scripting;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.util.List;
import java.util.stream.Collectors;

record StatementWithCondition(Expression condition, List<Statement> yes, List<Statement> no) implements Statement {

    @Override
    public void execute(EvaluationContext context) throws Break, Continue, Return {
        for (var statement : Boolean.TRUE.equals(condition.getValue(context, Boolean.class)) ? yes : no) {
            statement.execute(context);
        }
    }

    @Override
    @JsonValue
    public String toString() {
        var yesString = yes.stream().map("; %s"::formatted).collect(Collectors.joining());
        var noString = no.stream().map("; %s"::formatted).collect(Collectors.joining());
        return "if %s%s; else%s; fi".formatted(condition.getExpressionString(), yesString, noString);
    }

}
