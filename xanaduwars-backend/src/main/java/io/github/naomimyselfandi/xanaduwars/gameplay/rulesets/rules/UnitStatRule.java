package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.UnitStat;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.UnitStatQuery;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A rule that modifies one of a unit's stats.
public record UnitStatRule(
        @Override @NotNull @Valid Filter<Unit> subjectFilter,
        @NotNull UnitStat stat,
        int amount
) implements SubjectRule<UnitStatQuery, Integer, Unit> {

    @Override
    public Integer handle(UnitStatQuery query, Integer value) {
        return value + amount;
    }

    @Override
    public boolean handles(UnitStatQuery query, Integer value) {
        return SubjectRule.super.handles(query, value) && query.stat() == stat;
    }

}
