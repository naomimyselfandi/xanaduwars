package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.core.scripting.ScriptingException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.expression.EvaluationContext;

import java.util.List;
import java.util.stream.Collectors;

record StatementWithBlock(@Unmodifiable List<Statement> statements) implements Statement {

    StatementWithBlock(List<Statement> statements) {
        this.statements = List.copyOf(statements);
    }

    @Override
    public @Nullable Object execute(EvaluationContext context, Class<?> type) {
        for (var statement : statements) {
            try {
                var result = statement.execute(context, type);
                if (!PROCEED.equals(result)) return result;
            } catch (RuntimeException e) {
                throw new ScriptingException("Failed executing `%s`.".formatted(statement), e);
            }
        }
        return PROCEED;
    }

    @Override
    @JsonValue
    public String toString() {
        return statements.stream().map(Statement::toString).collect(Collectors.joining("\n"));
    }

}
