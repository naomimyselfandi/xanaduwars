package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;

import java.util.List;
import java.util.stream.Collectors;

record ScriptImpl(List<Statement> statements) implements Script {

    ScriptImpl(List<Statement> statements) {
        this.statements = List.copyOf(statements);
    }

    @Override
    public @Nullable Object run(EvaluationContext context) {
        for (var statement : statements) {
            try {
                statement.execute(context);
            } catch (Statement.Return e) {
                return e.value;
            } catch (Exception | Statement.Break | Statement.Continue e) {
                var root = context.getRootObject().getValue();
                var message = "Failed executing `%s` for %s in `%s`.".formatted(statement, root, this);
                throw new RuntimeException(message, e);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return statements.stream().map("%s;"::formatted).collect(Collectors.joining(" "));
    }

}
