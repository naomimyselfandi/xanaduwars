package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackCalculation;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Scalar;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A rule that modifies the damage a unit receives or inflicts.
public record AttackMultiplierRule(
        @Override @NotNull @Valid Filter<Unit> subjectFilter,
        @Override @NotNull @Valid BiFilter<Unit, Node> targetFilter,
        @NotNull Scalar multiplier
) implements TargetRule<AttackCalculation, Scalar, Unit, Node> {

    @Override
    public Scalar handle(AttackCalculation query, Scalar value) {
        return Scalar.withDoubleValue(value.doubleValue() * multiplier.doubleValue());
    }

}
