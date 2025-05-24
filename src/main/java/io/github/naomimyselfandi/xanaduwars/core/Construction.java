package io.github.naomimyselfandi.xanaduwars.core;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;

/// An incomplete structure.
/// @param type The type of structure being built.
/// @param progress How close the structure is to completion.
public record Construction(TileType type, Percent progress) {}
