package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/// A closed range, expressed as a pair of integers.
@JsonDeserialize(using = RangeDeserializer.class)
public record Range(@PositiveOrZero int min, @PositiveOrZero int max) implements Predicate<Integer> {

    /// The range `1-1`.
    public static final Range MELEE = new Range(1, 1);

    /// Check if this range is consistent. A range is inconsistent if its
    /// maximum is less than its minimum.
    @JsonIgnore
    @AssertTrue
    public boolean isConsistent() {
        return min <= max;
    }

    @Override
    public boolean test(Integer value) {
        return value >= min && value <= max;
    }

    @Override
    public @NotNull String toString() {
        return "%d-%d".formatted(min, max);
    }

}
