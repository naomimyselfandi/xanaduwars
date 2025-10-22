package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.annotation.JsonValue;

/// A tag that categorizes abilities.
public record AbilityTag(@JsonValue String name) {}
