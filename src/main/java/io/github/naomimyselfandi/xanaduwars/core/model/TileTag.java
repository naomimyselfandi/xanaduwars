package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.annotation.JsonValue;

/// A tag that categorizes tiles.
public record TileTag(@JsonValue String name) {}
