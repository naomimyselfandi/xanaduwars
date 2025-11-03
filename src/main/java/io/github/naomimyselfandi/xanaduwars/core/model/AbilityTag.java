package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;

/// A tag that categorizes abilities.
@NotCovered // trivial
public record AbilityTag(@JsonValue String name) {}
