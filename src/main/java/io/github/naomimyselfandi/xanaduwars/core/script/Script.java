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

    /// Parse an inline script. The argument is assumed to be an expression
    /// to be returned; i.e. for any string `x`, `Script.of(x)` is strictly
    /// equivalent to `Script.of("return(", x, ")")`.
    @JsonCreator
    static Script of(String code) {
        // of("return(" + code + ")") would do the wrong thing if the input
        // contains a comment. Using a single StatementOfExpression for the
        // script body, as ofConstant does, would do the wrong thing if the
        // input assigns to local variables (admittedly, this is unlikely).
        return of(List.of("return(", code, ")"));
    }

    /// Create a script with a constant integer value.
    @JsonCreator
    static Script of(int constant) {
        return ofConstant(constant);
    }

    /// Create a script with a constant double value.
    @JsonCreator
    static Script of(double constant) {
        return ofConstant(constant);
    }

    /// Create a script with a constant boolean value.
    @JsonCreator
    static Script of(boolean constant) {
        return ofConstant(constant);
    }

    private static Script ofConstant(Object constant) {
        return new ScriptImpl(new StatementOfExpression(constant.toString()));
    }

}
