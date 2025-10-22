package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

/// A function used in scripting.
@FunctionalInterface
@NotCovered // Mockito coverage bug workaround
public interface Function {

    /// Call this function with the given arguments.
    @Nullable Object call(@Nullable Object... arguments);

    /// Bind this function's leading arguments. This creates a new function
    /// that, when called, calls this function with the concatenation of the
    /// bound arguments and any additional arguments given to it.
    default Function bind(@Nullable Object... boundArguments) {
        return (givenArguments) -> {
            var arguments = Stream.concat(Arrays.stream(boundArguments), Arrays.stream(givenArguments)).toArray();
            return call(arguments);
        };
    }

}
