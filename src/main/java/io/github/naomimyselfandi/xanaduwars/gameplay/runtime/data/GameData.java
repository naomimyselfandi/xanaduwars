package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.PlayerId;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Version;

/// Low-level data about a game state.
@Getter
@Setter
@Entity
@DiscriminatorValue("GAME")
@ToString(callSuper = true)
@FieldNameConstants(asEnum = true)
public class GameData extends LowLevelData {

    /// The version of the game rules in use.
    @Embedded
    private @NotNull @Valid Version version;

    /// The current turn number.
    private @PositiveOrZero int turn;

    /// The index of the active player.
    @Embedded
    @AttributeOverride(name = "intValue", column = @Column(name = "active_player"))
    private @NotNull @Valid PlayerId activePlayer;

    @Override
    public int hashCode() {
        return GameData.class.hashCode();
    }

}
