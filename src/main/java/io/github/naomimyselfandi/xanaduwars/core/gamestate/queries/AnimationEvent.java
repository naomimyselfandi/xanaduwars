package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Tile;
import io.github.naomimyselfandi.xanaduwars.core.scripting.SimpleEvent;

/// An event that plays an animation.
public record AnimationEvent(Tile subject, Animation animation) implements SimpleEvent {}
