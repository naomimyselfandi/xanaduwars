package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackCalculation;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Scalar;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A rule that applies terrain effects to combat damage.
public record AttackTerrainRule(
        @Override @NotNull @Valid Filter<Unit> subjectFilter,
        @Override @NotNull @Valid BiFilter<Unit, Node> targetFilter
) implements TargetRule<AttackCalculation, Scalar, Unit, Node> {

    @Override
    public boolean handles(AttackCalculation query, Scalar value) {
        return query.target() instanceof Unit unit && unit.tile().isPresent();
    }

    @Override
    public Scalar handle(AttackCalculation query, Scalar value) {
        var multiplier = query.target().tile().orElseThrow().cover();
        return Scalar.withDoubleValue(value.doubleValue() * multiplier.doubleValue());
    }

}
