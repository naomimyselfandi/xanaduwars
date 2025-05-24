package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Node;
import io.github.naomimyselfandi.xanaduwars.core.Player;

/// A query that checks whether a player can see a node.
public record VisionQuery(@Override Player subject, @Override Node target) implements TargetQuery.Validation {}
