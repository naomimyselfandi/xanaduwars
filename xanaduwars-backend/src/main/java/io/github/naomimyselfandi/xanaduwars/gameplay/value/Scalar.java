package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.Contract;

/// A scalar value. This is a simple wrapper around a `double`, except that a
/// scalar in the range `[0, 1]` is always represented as a [Percent] instead.
public sealed interface Scalar extends Comparable<Scalar> permits Percent, ScalarNotPercent {

    /// View this scalar as a native `double`.
    @JsonValue
    double doubleValue();

    /// Clamp this scalar to the range `[0%, 100%]`.
    @Contract(pure = true)
    Percent clamp();

    /// View a native `double` as a [Scalar].
    @JsonCreator
    static Scalar withDoubleValue(double doubleValue) {
        if (doubleValue >= 0 && doubleValue <= 1) {
            return new Percent(doubleValue);
        } else {
            return new ScalarNotPercent(doubleValue);
        }
    }

    @Override
    default int compareTo(Scalar that) {
        return Double.compare(this.doubleValue(), that.doubleValue());
    }

}
