package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackCalculation;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Percent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Scalar;

/// A rule that multiplies a unit's damage by its HP.
public record AttackHpRule() implements GameRule<AttackCalculation, Scalar> {

    @Override
    public boolean handles(AttackCalculation query, Scalar value) {
        return !query.subject().hp().equals(Percent.FULL); // Cleaner auditing
    }

    @Override
    public Scalar handle(AttackCalculation query, Scalar value) {
        return Scalar.withDoubleValue(query.subject().hp().doubleValue() * value.doubleValue());
    }

}
