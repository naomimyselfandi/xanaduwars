package io.github.naomimyselfandi.xanaduwars.core;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;

/// A game state.
public interface GameState extends QueryEvaluator {

    /// The ruleset used by this game state.
    Ruleset ruleset();

    /// Construct some player's perspective of this game state. This game state
    /// includes only the information the player has access to. It is otherwise
    /// a legitimate game state in its own right. If it is modified, the
    /// modification is not reflected in this game state.
    GameState asSeenBy(Player player);

    /// Look up the actions an element can use.
    <S extends Element> List<Action<? super S, ?>> actions(S user);

    /// Instruct an element to use some actions.
    <S extends Element> void execute(List<ActionItem<S, ?>> items, S user) throws ActionException;

    /// The current turn number. This increases after each full turn cycle, not
    /// after each player's turn.
    int turn();

    /// The player currently taking their turn.
    Player activePlayer();

    /// Start the next player's turn.
    void pass();

    /// This game state's players in turn order.
    @Unmodifiable List<Player> players();

    /// This game state's tiles in row-column order.
    @Unmodifiable List<@Unmodifiable List<Tile>> tiles();

    /// Get a tile by its coordinates.
    Optional<Tile> tile(int x, int y);

    /// This game state's units in creation order.
    @Unmodifiable List<Unit> units();

    /// Get a unit by its ID.
    Optional<Unit> unit(int id);

}
