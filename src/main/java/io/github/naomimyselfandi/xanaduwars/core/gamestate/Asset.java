package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.AssetType;

/// An element that can be attacked. Structures and units are both assets.
public sealed interface Asset extends Physical permits Structure, Unit {

    /// Get this asset's type.
    AssetType getType();

    /// Get this asset's current HP.
    Hp getHp();

    /// Set this asset's current HP.
    Asset setHp(Hp hp);

    /// Get this asset's vision range.
    int getVision();

}
