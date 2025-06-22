package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import org.springframework.expression.EvaluationContext;

import java.util.List;

record StatementWithLambda(String name, String signature, List<String> parameters, Script body) implements Statement {

    StatementWithLambda(String name, String signature, List<String> parameters, Script body) {
        this.name = name;
        this.signature = signature;
        this.parameters = List.copyOf(parameters);
        this.body = body;
    }

    @Override
    public Object execute(EvaluationContext context, Class<?> type) {
        var sig = context.getTypeLocator().findType(signature);
        context.setVariable(name, LambdaProxyFactory.create(name, body, context, parameters, sig));
        return PROCEED;
    }

    @Override
    @JsonValue
    public String toString() {
        var params = String.join(", ", parameters);
        return "lambda #%s:%s(%s):\n%s".formatted(name, signature, params, ScriptImpl.indent(body));
    }

}
