package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Map;
import java.util.Objects;

record ScriptImpl(Statement body) implements Script {

    @Override
    public @Nullable Object execute(ScriptRuntime runtime, Map<String, Object> arguments) {
        return execute(runtime, arguments, null);
    }

    @Override
    public @Nullable Object execute(
            ScriptRuntime runtime,
            Map<String, Object> arguments,
            @Nullable TypeDescriptor type
    ) {
        var context = new ScriptRootContext(runtime);
        arguments.forEach(context::setVariable);
        var result = body.execute(runtime, context);
        if (type == null) {
            return result;
        } else {
            return context.getTypeConverter().convertValue(result, TypeDescriptor.forObject(result), type);
        }
    }

    @Override
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T> T executeNotNull(ScriptRuntime runtime, Map<String, Object> arguments, T... typeHint) {
        var result = (T) execute(runtime, arguments, TypeDescriptor.valueOf(typeHint.getClass().getComponentType()));
        return Objects.requireNonNull(result);
    }

    @Override
    public @Unmodifiable Library executeAsLibrary(ScriptRuntime runtime) {
        var context = new ScriptRootContext(runtime);
        var _ = body.execute(runtime, context);
        context.variables.keySet().removeIf(it -> it.startsWith("_"));
        context.variables.values().removeIf(Objects::isNull);
        return new LibraryImpl(context.variables);
    }

}
