package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.xanaduwars.core.common.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// The ruleset associated with some version of the game.
public interface Ruleset extends GlobalRuleSource {

    /// This ruleset's version number.
    Version getVersion();

    /// The global rules in this ruleset.
    @Unmodifiable List<Rule> getRules();

    /// The non-weapon, non-spell actions in this ruleset.
    @Unmodifiable List<NormalAction> getActions();

    /// The commanders in this ruleset.
    @Unmodifiable List<Commander> getCommanders();

    /// Get a commander by ID.
    Commander getCommander(CommanderId id);

    /// The spellcasting affinities in this ruleset.
    @Unmodifiable List<Affinity> getAffinities();

    /// The spells in this ruleset.
    @Unmodifiable List<Spell> getSpells();

    /// Get a spell by ID.
    Spell getSpell(SpellId id);

    /// The structure types in this ruleset.
    @Unmodifiable List<StructureType> getStructureTypes();

    /// Get a structure type by ID.
    StructureType getStructureType(StructureTypeId id);

    /// The tile types in this ruleset.
    @Unmodifiable List<TileType> getTileTypes();

    /// Get a tile type by ID.
    TileType getTileType(TileTypeId id);

    /// The unit types in this ruleset.
    @Unmodifiable List<UnitType> getUnitTypes();

    /// Get a unit type by ID.
    UnitType getUnitType(UnitTypeId id);

    /// The action used for unit movement.
    Action getMoveAction();

    /// The action used for unloading a unit from a transport.
    Action getDropAction();

    /// An action that does nothing.
    Action getWaitAction();

    /// The action used to pass turn.
    Action getPassAction();

    /// The action used to yield.
    Action getYieldAction();

}
