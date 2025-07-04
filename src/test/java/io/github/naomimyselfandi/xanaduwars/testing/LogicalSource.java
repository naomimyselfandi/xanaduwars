package io.github.naomimyselfandi.xanaduwars.testing;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.*;
import java.util.function.BinaryOperator;

/// An argument source that generates a truth table. This may be used with any
/// `@ParameterizedTest` method that accepts three or more booleans. The first
/// `N-1` parameters (where `N` is the parameter count) represent conditions,
/// while the last represents the expected value, which is generated by applying
/// some boolean operation to the conditions.
@Documented
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(LogicalArgumentsProvider.class)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface LogicalSource {

    /// The logical operation to use while generating the expected value.
    Op value();

    /// Whether the operation should be negated.
    boolean negated() default false;

    /// A logical operation that may generate an expected value.
    @RequiredArgsConstructor
    enum Op {

        /// The `&&` operation.
        AND(Boolean::logicalAnd),

        /// The `||` operation.
        OR(Boolean::logicalOr),

        /// The `^` operation.
        XOR(Boolean::logicalXor);

        final BinaryOperator<Boolean> definition;

    }

}
