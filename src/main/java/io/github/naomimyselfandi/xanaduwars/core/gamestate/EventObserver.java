package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Event;

import java.util.function.Consumer;

/// A callback that receives game events. This is intended for use in logging,
/// animations, and so on; game logic should be handled by game rules.
public interface EventObserver extends Consumer<Event<?>> {}
