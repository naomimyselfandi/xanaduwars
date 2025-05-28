package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.validator.constraints.Range;

/// The ID of a player.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record PlayerId(@Column(name = "player_id") @JsonValue @Range(min = 0, max = 255) int intValue)
        implements Comparable<PlayerId> {

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
