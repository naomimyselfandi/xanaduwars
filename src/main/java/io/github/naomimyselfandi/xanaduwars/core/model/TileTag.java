package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;

/// A tag that categorizes tiles.
@NotCovered // trivial
public record TileTag(@JsonValue String name) {}
