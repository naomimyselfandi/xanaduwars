package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.core.scripting.ScriptingException;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;

import java.util.stream.Collectors;

record ScriptImpl(@JsonValue Statement body) implements Script {

    @Override
    public @Nullable Object run(EvaluationContext context, Class<?> type) {
        try {
            return switch (body.execute(context, type)) {
                case Statement.Proceed _ -> null;
                case Object it -> it;
                case null -> null;
            };
        } catch (RuntimeException e) {
            throw new ScriptingException("Failed getting %s from %s in %s.".formatted(type, body, context), e);
        }
    }

    static String indent(Object object) {
        return object.toString().lines().map("  %s"::formatted).collect(Collectors.joining("\n"));
    }

}
