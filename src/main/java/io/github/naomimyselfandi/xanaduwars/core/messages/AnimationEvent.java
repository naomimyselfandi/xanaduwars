package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Animation;
import io.github.naomimyselfandi.xanaduwars.core.model.Node;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;

/// An event indicating that the UI should play an animation. Game rules don't
/// ordinarily react to animation events.
@NotCovered // Trivial
public record AnimationEvent(Node node, Animation animation) implements SimpleEvent {}
