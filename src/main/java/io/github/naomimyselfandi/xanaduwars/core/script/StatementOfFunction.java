package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.expression.EvaluationContext;

import java.util.List;

record StatementOfFunction(String name, @Unmodifiable List<String> parameters, Statement body) implements Statement {

    StatementOfFunction(String name, List<String> parameters, Statement body) {
        this.name = name;
        this.parameters = List.copyOf(parameters);
        this.body = body;
    }

    @Override
    public @Nullable Object execute(ScriptRuntime runtime, EvaluationContext context) {
        context.setVariable(name, new FunctionImpl(runtime, context, name, parameters, body));
        return null;
    }

}
