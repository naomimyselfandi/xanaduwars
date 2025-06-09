package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// The ruleset associated with some version of the game.
@JsonDeserialize(using = RulesetDeserializer.class)
public interface Ruleset extends GlobalRuleSource {

    /// The global rules in this ruleset.
    @Unmodifiable List<Rule> globalRules();

    /// The commanders in this ruleset.
    @Unmodifiable List<Commander> commanders();

    /// Get a commander by ID.
    Commander commander(CommanderId id);

    /// The spells in this ruleset.
    @Unmodifiable List<Spell> spells();

    /// Get a spell by ID.
    Spell spell(SpellId id);

    /// The structure types in this ruleset.
    @Unmodifiable List<StructureType> structureTypes();

    /// Get a structure type by ID.
    StructureType structureType(StructureTypeId id);

    /// The tile types in this ruleset.
    @Unmodifiable List<TileType> tileTypes();

    /// Get a tile type by ID.
    TileType tileType(TileTypeId id);

    /// The unit types in this ruleset.
    @Unmodifiable List<UnitType> unitTypes();

    /// Get a unit type by ID.
    UnitType unitType(UnitTypeId id);

    /// Actions automatically available to units, like movement.
    @Unmodifiable List<Action> commonUnitActions();

    /// Actions automatically available to players, like ending their turn.
    @Unmodifiable List<Action> commonPlayerActions();

    /// The action that deploys a unit from a structure.
    Action deploymentAction();

}
