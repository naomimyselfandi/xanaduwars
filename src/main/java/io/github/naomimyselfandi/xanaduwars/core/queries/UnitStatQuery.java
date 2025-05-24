package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Unit;

/// A request to calculate a statistic of a unit.
public record UnitStatQuery(@Override Unit subject, UnitStat stat) implements SubjectQuery<Integer> {}
