package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.jetbrains.annotations.Contract;

/// A percent in the range `[0%, 100%]`.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Percent(@Override @Column(name = "percent") double doubleValue) implements Scalar {

    /// The percent `0%`.
    public static final Percent ZERO = new Percent(0);

    /// The percent `100%`.
    public static final Percent FULL = new Percent(1);

    /// {@inheritDoc}
    @Override
    @Contract(pure = true, value = "-> this")
    public Percent clamp() {
        return this;
    }

    /// Convert a double to a percentage.
    /// @throws IllegalArgumentException if the given value is out of range or
    /// `NaN`.
    @JsonCreator
    public static Percent withDoubleValue(double doubleValue) {
        return new Percent(doubleValue);
    }

    /// Get the percent that's closest to a double value. Values that don't
    /// correspond to the range `[0%, 100%]` are clamped to that range without
    /// any exception.
    public static Percent clamp(double doubleValue) {
        if (doubleValue <= 0) {
            return ZERO;
        } else if (doubleValue >= 1) {
            return FULL;
        } else {
            return new Percent(doubleValue);
        }
    }

    /// A percent in the range `[0%, 100%]`.
    /// @throws IllegalArgumentException if the given value is out of range or
    /// `NaN`.
    public Percent {
        if (doubleValue < 0 || doubleValue > 1 || Double.isNaN(doubleValue)) {
            throw new IllegalArgumentException("Invalid percent %s.".formatted(doubleValue));
        }
    }

}
