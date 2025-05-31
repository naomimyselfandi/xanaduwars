package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.PlayerId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/// A player.
public non-sealed interface Player extends Element {

    /// The commander this player is playing as.
    @Override
    Commander type();

    /// This player's ID. This corresponds to their position in the turn order.
    PlayerId id();

    /// This player's team. In a non-team game, this is an arbitrary unique
    /// integer.
    int team();

    /// Whether this player has been defeated.
    boolean defeated();

    /// Defeat this player.
    void defeat();

    /// Check if this unit can see a tile or unit.
    boolean canSee(Node node);

    /// The units owned by this player, in creation order.
    Stream<Unit> units();

    /// The tiles owned by this player, in row-column order.
    Stream<Tile> tiles();

    /// This player's current resources.
    @Unmodifiable Map<Resource, Integer> resources();

    /// Update one of this player's resource totals.
    void resource(Resource resource, int quantity);

    /// The spells this player knows.
    @Unmodifiable List<SpellType<?>> knownSpells();

    /// The spells this player has cast this turn.
    @Unmodifiable List<Spell> activeSpells();

    /// Record that a player cast a spell this turn.
    void addActiveSpell(SpellType<?> spellType);

    /// Clear this player's active spell list.
    void clearActiveSpells();

}
