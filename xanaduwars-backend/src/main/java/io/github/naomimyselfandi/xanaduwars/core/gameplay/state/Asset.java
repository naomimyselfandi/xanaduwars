package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import org.jetbrains.annotations.Nullable;

/// An element owned by a player. Assets have HP and can be attacked.
public sealed interface Asset extends Actor, Physical permits Structure, Unit {

    /// Full HP. HP is measured as a percentage, so this is always 100.
    int FULL_HP = 100;

    /// The player that owns this asset.
    @Nullable Player owner();

    /// This asset's current HP. This is a percentage, with 100 indicating that
    /// the asset is at full HP and zero indicating that it is about to be
    /// destroyed.
    int hp();

    /// This asset's current HP. If the given value is greater than 100, it is
    /// normalized to 100. If it is zero or less, the asset is destroyed.
    Asset hp(int hp);

    /// How far this asset can see.
    int vision();

    /// Check if this asset can see a tile.
    boolean canSee(Tile tile);

    /// The terrain this asset is on. This is `null` if and only if this asset
    /// is a unit which is inside another unit.
    @Nullable Terrain terrain();

}
