package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.Direction;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Version;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Rule;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A version of the game rules.
public interface Ruleset {

    /// This ruleset's version number.
    Version version();

    /// The global rules declared by this ruleset.
    @Unmodifiable List<Rule<?, ?>> globalRules();

    /// The playable commanders declared by this ruleset.
    @Unmodifiable List<Commander> commanders();

    /// The spell types declared by this ruleset.
    @Unmodifiable List<SpellType<?>> spellTypes();

    /// The tile types declared by this ruleset.
    @Unmodifiable List<TileType> tileTypes();

    /// The unit types declared by this ruleset.
    @Unmodifiable List<UnitType> unitTypes();

    /// Miscellaneous details about this ruleset.
    Details details();

    /// Miscellaneous details about a ruleset.
    interface Details {

        /// The action that causes a unit to move.
        Action<Unit, List<Direction>> moveAction();

        /// The action that causes a transport to drop its cargo.
        Action<Unit, Direction> dropAction();

        /// The action that causes a unit to attack.
        Action<Unit, Node> attackAction();

        /// The action that causes a unit to do nothing.
        Action<Unit, None> waitAction();

        /// The action that causes a tile to deploy a unit.
        Action<Tile, UnitType> deployAction();

        /// The action that causes a player to pass turn.
        Action<Player, None> passAction();

        /// The action that causes a player to resign.
        Action<Player, None> resignAction();

    }

}
