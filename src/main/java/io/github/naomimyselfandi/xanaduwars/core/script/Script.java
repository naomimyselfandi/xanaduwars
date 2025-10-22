package io.github.naomimyselfandi.xanaduwars.core.script;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;
import java.util.Map;

/// A script used to define a game rules, spell, or ability.
@NotCovered // Mockito coverage bug workaround
public interface Script {

    /// A script that always returns zero.
    Script ZERO = of(0);

    /// A script that always returns `true`.
    Script TRUE = of(true);

    /// Execute this script.
    @Nullable Object execute(ScriptRuntime runtime, Map<String, Object> arguments);

    /// Execute this script, optionally specifying a return type.
    @Nullable Object execute(ScriptRuntime runtime, Map<String, Object> arguments, @Nullable TypeDescriptor type);

    /// Executing this script, inferring a non-null return type.
    @SuppressWarnings("unchecked")
    <T> T executeNotNull(ScriptRuntime runtime, Map<String, Object> arguments, T... typeHint);

    /// Execute this script as a library. This behaves identically to
    /// [#execute(ScriptRuntime, Map)] called wih an empty `Map`, except that
    /// the return value is a library containing the script's variables.
    /// Variables starting with an underscore are omitted, providing access
    /// control.
    @Unmodifiable Library executeAsLibrary(ScriptRuntime runtime);

    /// Parse a script.
    @JsonCreator
    static Script of(List<String> code) {
        return ScriptParser.parse(code);
    }

    /// Parse a script.
    @JsonCreator
    static Script of(String code) {
        return of(code.lines().toList());
    }

    /// Create a script with a constant integer value.
    @JsonCreator
    static Script of(int constant) {
        return of("return(%d)".formatted(constant));
    }

    /// Create a script with a constant boolean value.
    @JsonCreator
    static Script of(boolean constant) {
        return of("return(%s)".formatted(constant));
    }

}
