package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.PlayerId;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/// A low-level description of an asset. Units and structures are both assets.
public interface AssetData extends Serializable {

    /// The ID of the asset's owner.
    @Nullable PlayerId owner();

    /// The asset's current hit points.
    int hp();

    /// The asset's current hit points.
    AssetData hp(int hp);

}
