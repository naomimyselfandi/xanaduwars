package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;

import java.util.List;

/// A SpEl method resolver that provides flow control.
final class FlowMethodResolver implements MethodResolver {

    private static final MethodExecutor GOTO = (context, _, arguments) -> {
        context.setVariable(StatementOfBlock.TARGET_VARIABLE, arguments[0]);
        return TypedValue.NULL;
    };

    private static final MethodExecutor RETURN_0 = (context, _, _) -> {
        context.setVariable(StatementOfBlock.TARGET_VARIABLE, Integer.MAX_VALUE); // Jump past the end to exit.
        return TypedValue.NULL;
    };

    private static final MethodExecutor RETURN_1 = (context, _, arguments) -> {
        context.setVariable(StatementOfBlock.TARGET_VARIABLE, Integer.MAX_VALUE); // Jump past the end to exit.
        context.setVariable(StatementOfBlock.RESULT_VARIABLE, arguments[0]);
        return TypedValue.NULL;
    };

    @Override
    public @Nullable MethodExecutor resolve(
            EvaluationContext context,
            Object targetObject,
            String name,
            List<TypeDescriptor> types
    ) {
        if (name.equals("goto") && types.size() == 1 && types.getFirst().getObjectType() == Integer.class) {
            return GOTO;
        } else if (name.equals("return") && types.isEmpty()) {
            return RETURN_0;
        } else if (name.equals("return") && types.size() == 1) {
            return RETURN_1;
        } else {
            return null;
        }
    }

}
