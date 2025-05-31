package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TurnStartEventForUnit;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A rule that readies units at the start of their turn.
public record UnitReadyRule(
        @Override @NotNull @Valid Filter<Unit> subjectFilter
) implements SubjectRule<TurnStartEventForUnit, None, Unit> {

    @Override
    public None handle(TurnStartEventForUnit query, None value) {
        query.subject().canAct(true);
        return None.NONE;
    }

}
