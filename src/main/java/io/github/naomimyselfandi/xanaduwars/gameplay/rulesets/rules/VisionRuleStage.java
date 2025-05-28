package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.VisionQueryStage;

/// A rule that applies a filter on node visibility.
public record VisionRuleStage(
        @Override Filter<Node> subjectFilter,
        @Override BiFilter<Node, Node> targetFilter,
        @Override BiFilter<Node, Node> criterion
) implements TargetRule.Validator<VisionQueryStage, Node, Node> {

    @Override
    public boolean isValid(VisionQueryStage query) {
        return criterion.test(query.subject(), query.target());
    }

}
