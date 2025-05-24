package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Unit;

/// A request to validate whether a unit can move along a path.
public record PathValidation(@Override Unit subject, Path path) implements SubjectQuery.Validation {}
