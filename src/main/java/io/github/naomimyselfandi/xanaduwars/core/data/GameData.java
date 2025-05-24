package io.github.naomimyselfandi.xanaduwars.core.data;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.VersionNumber;

/// Low-level data about a game state.
@Getter
@Setter
@Entity
@DiscriminatorValue("GAME")
@ToString(callSuper = true)
@FieldNameConstants(asEnum = true)
public class GameData extends LowLevelData {

    /// The VersionNumber of the game rules in use.
    private @NotNull @Valid VersionNumber versionNumber;

    /// The current turn number.
    private @PositiveOrZero int turn;

    /// The index of the active player.
    private @NotNull @Valid PlayerId activePlayer;

    @Override
    public int hashCode() {
        return GameData.class.hashCode();
    }

}
