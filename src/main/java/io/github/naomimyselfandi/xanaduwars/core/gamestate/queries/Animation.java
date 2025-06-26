package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Direction;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Physical;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Tile;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Event;

import java.util.function.Function;

/// An animation.
public enum Animation {

    NORTH,

    EAST,

    SOUTH,

    WEST,

    EXPLOSION,

    ATTACK,

    BUFF,

    DEBUFF;

    private final Function<Tile, Event<?>> eventFactory = tile -> new AnimationEvent(tile, this);

    /// Get the animation representing movement in a direction.
    public static Animation of(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
        };
    }

    /// Play this animation on a physical element.
    /// @apiNote This method is a convenience for scripting.
    public void play(Physical physical) {
        physical.getTile().map(eventFactory).ifPresent(physical.getGameState()::evaluate);
    }

}
