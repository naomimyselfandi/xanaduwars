package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.*;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Event;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.SortedMap;

/// A game state.
public interface GameState {

    /// Get the ruleset in use by this game state.
    Ruleset getRuleset();

    /// Evaluate a query against this game state.
    <T> T evaluate(Query<T> query);

    /// Invalidate any cached queries. The query cache is automatically
    /// invalidated before evaluating an [Event], so it is only necessary to
    /// call this when the game changes without creating an event.
    void invalidateCache();

    /// Attach an event observer to this game state. Whenever this game state
    /// finishes evaluates an event, the observer is notified of it.
    GameState attachObserver(EventObserver eventObserver);

    /// Create a copy of this game state, limited to what the given player can
    /// see. The copy is otherwise a complete game state in its own right, and
    /// changes to a copy are not reflected in the original nor vice versa.
    GameState limitedTo(Player player);

    /// Check whether this is a limited copy of another game state. A limited
    /// copy contains only what a given player can see; they're used in
    /// rendering, previews, and validation. A limited copy is otherwise a game
    /// state in its own right, and changes to a copy are not reflected in the
    /// original nor vice versa.
    boolean isLimitedCopy();

    /// Get this game state's current turn number.
    Turn getTurn();

    /// Set this game state's current turn number.
    GameState setTurn(Turn turn);

    /// Check if the active player has passed turn.
    boolean isPassed();

    /// Set whether the active player has passed turn.
    GameState setPassed(boolean passed);

    /// Get this game state's players in turn order.
    @UnmodifiableView List<Player> getPlayers();

    /// Get this game state's tiles in row-column order.
    @UnmodifiableView SortedMap<StructureId, Structure> getStructures();

    /// Get this game state's structures in row-column order.
    @UnmodifiableView SortedMap<TileId, Tile> getTiles();

    /// Get this game state's units in creation order.
    @UnmodifiableView SortedMap<UnitId, Unit> getUnits();

    /// Get the currently active player.
    /// @implSpec This is equivalent to `p.get(t % p.size())`, where `p` is the
    /// [list of players][#getPlayers()], and `t` is the integer value of the
    /// [current turn number][#getTurn()].
    Player getActivePlayer();

    /// Create a structure on a tile.
    /// @throws IllegalStateException if the tile already has a structure.
    Structure createStructure(Tile tile, StructureType type);

    /// Create a unit on a tile.
    /// @throws IllegalStateException if the tile already has a unit.
    Unit createUnit(Tile tile, UnitType type);

    /// Get the unit at a location, if any.
    @Nullable Unit getUnit(Node location);

    /// Move a unit to a new location.
    void moveUnit(Unit unit, Node destination);

}
