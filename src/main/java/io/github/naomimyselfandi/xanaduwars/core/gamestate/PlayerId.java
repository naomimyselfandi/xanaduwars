package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;
import org.jetbrains.annotations.NotNull;

/// The ID of a player in a game. A player's ID is also their position in the
/// turn order: the starting player has an ID of zero, the next player has an
/// ID of one, and so on.
@Embeddable
@JsonDeserialize(keyUsing = PlayerIdKeyDeserializer.class)
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record PlayerId(@Column(name = "player") @PositiveOrZero @JsonValue int playerId)
        implements ElementId, Comparable<PlayerId> {

    @Override
    public int compareTo(@NotNull PlayerId that) {
        return Integer.compare(this.playerId, that.playerId);
    }

}
