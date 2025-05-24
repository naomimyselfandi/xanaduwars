package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.hibernate.validator.constraints.Range;

/// The ID of a player.
public record PlayerId(@Range(min = 0, max = 255) @Override @JsonValue int intValue)
        implements IntWrapper, Comparable<PlayerId> {

    /// The ID of a player.
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public PlayerId {}

    /// Helper so Jackson can use these as map keys.
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private PlayerId(String string) {
        this(Integer.parseInt(string));
    }

    @Override
    public int compareTo(PlayerId that) {
        return Integer.compare(this.intValue, that.intValue);
    }

    @Override
    public String toString() {
        return "Player(%d)".formatted(intValue);
    }

}
