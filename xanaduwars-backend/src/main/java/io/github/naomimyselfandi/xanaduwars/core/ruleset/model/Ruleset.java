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
    @Unmodifiable List<Rule> getGlobalRules();

    /// The commanders in this ruleset.
    @Unmodifiable List<Commander> getCommanders();

    /// Get a commander by ID.
    Commander getCommander(CommanderId id);

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
    TileType getTileTypes(TileTypeId id);

    /// The unit types in this ruleset.
    @Unmodifiable List<UnitType> getUnitTypes();

    /// Get a unit type by ID.
    UnitType getUnitType(UnitTypeId id);

    /// Actions automatically available to units, like movement.
    @Unmodifiable List<Action> getCommonUnitActions();

    /// Actions automatically available to players, like ending their turn.
    @Unmodifiable List<Action> getCommonPlayerActions();

    /// The action that deploys a unit from a structure.
    Action getDeploymentAction();

}
