package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.UnitStat;

/// A request to calculate a statistic of a unit.
public record UnitStatQuery(@Override Unit subject, UnitStat stat) implements SubjectQuery<Integer, Unit> {

    @Override
    public Integer defaultValue() {
        return switch (stat) {
            case VISION -> subject.type().vision();
            case SPEED -> subject.type().speed();
            case MIN_RANGE -> subject.type().range().min();
            case MAX_RANGE -> subject.type().range().max();
        };
    }

}
