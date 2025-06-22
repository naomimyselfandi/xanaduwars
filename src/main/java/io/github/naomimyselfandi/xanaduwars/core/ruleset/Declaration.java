package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import io.github.naomimyselfandi.xanaduwars.core.common.Tag;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/// An independent value declared by a ruleset.
public sealed interface Declaration permits AssetType, Commander, NormalAction, Spell, TerrainType {

    /// This declaration's unique name.
    Name getName();

    /// Any tags that apply to this declaration.
    @Unmodifiable Set<? extends Tag> getTags();

}
