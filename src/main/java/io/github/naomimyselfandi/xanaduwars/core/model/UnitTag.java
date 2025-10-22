package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.annotation.JsonValue;

/// A tag that categorizes units.
public record UnitTag(@JsonValue String name) {}
