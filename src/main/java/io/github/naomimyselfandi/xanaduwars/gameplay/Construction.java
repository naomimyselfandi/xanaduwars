package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;

/// An incomplete structure.
/// @param type The type of structure being built.
/// @param progress How close the structure is to completion.
public record Construction(TileType type, Percent progress) {}
