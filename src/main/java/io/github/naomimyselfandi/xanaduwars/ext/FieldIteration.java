package io.github.naomimyselfandi.xanaduwars.ext;

import lombok.experimental.UtilityClass;

/// A helper for *field iteration*. Lombok has the ability to generate an enum
/// with one constant for each of a type's fields, and switch expressions over
/// enums require a branch for each enum constant at compile time. Using these
/// features together allows us to statically guarantee that we're processing
/// each field, without any chance of forgetting one, but the required syntax is
/// a little strange. This utility exists to clean it up.
@UtilityClass
public class FieldIteration {

    /// A processor that runs for each of a type's fields.
    /// @param <T> The enum used to represent the type's fields.
    @FunctionalInterface
    @SuppressWarnings("UnusedReturnValue")
    public interface FieldProcessor<T extends Enum<T>> {

        /// Process a field.
        ///
        /// @apiNote The return value of this method has no semantic meaning.
        /// However, the most obvious way to implement this method is with a
        /// lambda like `field -> switch (field) {...}`; making the return type
        /// non-`void` causes such a lambda to be interpreted as a switch
        /// expression, which is desirable because switch statements don't
        /// enforce completeness.
        ///
        /// @return An arbitrary object.
        Object process(T field);

    }

    /// A helper for *field iteration*. Lombok has the ability to generate an
    /// enum with one constant for each of a type's fields, and switch
    /// expressions over enums require a branch for each enum constant at
    /// compile time. Using these features together allows us to statically
    /// guarantee that we're processing each field, without any chance of
    /// forgetting one. This method does just that.
    public static <T extends Enum<T>> void forEachField(T[] fields, FieldProcessor<T> processor) {
        for (var field : fields) processor.process(field);
    }

}
