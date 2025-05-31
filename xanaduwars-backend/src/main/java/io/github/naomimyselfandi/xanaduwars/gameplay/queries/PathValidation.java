package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Path;

/// A request to validate whether a unit can move along a path.
public record PathValidation(@Override Unit subject, Path path) implements SubjectQuery.Validation<Unit> {}
