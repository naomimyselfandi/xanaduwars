package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;

/// A query that checks whether one node can see another.
public record VisionQueryStage(@Override Node subject, @Override Node target)
        implements TargetQuery.Validation<Node, Node> {}
