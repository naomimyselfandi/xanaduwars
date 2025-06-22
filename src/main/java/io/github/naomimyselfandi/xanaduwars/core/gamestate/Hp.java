package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.util.Ordinal;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.util.List;
import java.util.stream.IntStream;

/// A unit or structure's hit points. HP values range from 0 to 100, allowing
/// them to also function as percentages; various game rules scale off of a unit
/// or structure's remaining HPP.
@Slf4j
@Embeddable
@FieldNameConstants
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Hp(@Column(name = "hp") @JsonValue @Range(min = 0, max = 100) int ordinal)
        implements Serializable, Ordinal<Hp> {

    private static final List<Hp> VALUES = IntStream.rangeClosed(0, 100).mapToObj(Hp::new).toList();

    /// Zero HP.
    public static final Hp ZERO = VALUES.getFirst();

    /// One hundred HP.
    public static final Hp FULL = VALUES.getLast();

    /// Convert an integer to an HP value.
    public static Hp fromOrdinal(int ordinal) {
        if (ordinal < 0) {
            return VALUES.getFirst();
        } else if (ordinal > 100) {
            return VALUES.getLast();
        } else {
            return VALUES.get(ordinal);
        }
    }

    /// Canonical constructor.
    /// @deprecated Use [#fromOrdinal(int)] or one of the static constants.
    /// (This constructor would be `private` if it could be.)
    /// @throws IllegalArgumentException if the value is not in the range
    /// `[0, 100]`.
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public Hp {
        if (ordinal < 0 || ordinal > 100) {
            throw new IllegalArgumentException("Invalid HP value %d.".formatted(ordinal));
        }
    }

    @Override
    public Hp withOrdinal(int ordinal) {
        return fromOrdinal(ordinal);
    }

    @Override
    public String toString() {
        return String.valueOf(ordinal());
    }

}
