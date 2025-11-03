package io.github.naomimyselfandi.xanaduwars.core.model;

import io.github.naomimyselfandi.xanaduwars.core.message.MessageBus;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.core.service.CopyMachine;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.stream.Stream;

/// A game state.
public interface GameState extends ScriptRuntime, MessageBus {

    /// Get the version of the game used by this game state.
    Version getVersion();

    /// Check if this game state has been redacted.
    /// @see CopyMachine#createRedactedCopy(GameState, Player)
    boolean isRedacted();

    /// Get the current turn.
    int getTurn();

    /// Set the current turn.
    GameState setTurn(int turn);

    /// Get this game state's players in turn order.
    @Unmodifiable List<Player> getPlayers();

    /// Get the player currently taking their turn. This is equivalent to
    /// `getPlayers().get(getPlayers().size() % getTurn())`.
    Player getActivePlayer();

    /// Get a player by their position in the turn order.
    Player getPlayer(int position);

    /// Get the width of the playable area.
    int getWidth();

    /// Get the height of the playable area.
    int getHeight();

    /// Get this game state's tiles in row-column order.
    @Unmodifiable List<Tile> getTiles();

    /// Get a tile by its coordinates. `null` is returned if and only if the
    /// given coordinates are out of bounds.
    @Nullable Tile getTile(int x, int y);

    /// Get this game state's units in location order.
    Stream<Unit> getUnits();

    /// Submit a sequence of commands for the active player to perform.
    boolean submitPlayerCommand(List<Command> commands) throws CommandException;

    /// Submit a sequence of commands for a unit to perform.
    boolean submitUnitCommand(int x, int y, List<Command> commands) throws CommandException;

}
