package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.GameStateId;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.stream.Stream;

/// A game state.
public interface GameState {

    /// This game state's unique ID.
    @Nullable GameStateId id();

    /// Evaluate a query. Every rule applying to the query is checked, starting
    /// with the global rules.
    <T> T evaluate(Query<T> query);

    /// Whether this game state is being used to preview an action.
    boolean preview();

    /// Indicate that the active player's turn should end. This is deferred
    /// until the end of action processing to avoid edge cases.
    void pass();

    /// The players in this game state, in turn order.
    @Unmodifiable List<Player> players();

    /// Get a player by ID.
    Player player(PlayerId id);

    /// The player who is currently acting.
    Player activePlayer();

    /// The tiles in this game state, in row-column order.
    @Unmodifiable List<Tile> tiles();

    /// Get a tile by ID.
    Tile tile(TileId id);

    /// Get a tile by ID, if it is in this game state's bounds.
    @Nullable Tile maybeTile(TileId id);

    /// The structures in this game state, in row-column order.
    Stream<Structure> structures();

    /// The units in this game state, in creation order.
    Stream<Unit> units();

    /// Get a unit by ID.
    Unit unit(UnitId id);

}
