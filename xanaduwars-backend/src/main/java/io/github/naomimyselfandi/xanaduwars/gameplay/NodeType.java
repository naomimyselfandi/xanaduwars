package io.github.naomimyselfandi.xanaduwars.gameplay;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.NodeTypeId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import jakarta.validation.constraints.Positive;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/// A type of node. Units and tiles are both nodes.
public sealed interface NodeType extends Type permits TileType, UnitType {

    /// The cost to deploy a unit or build a structure of this type.
    @Unmodifiable Map<Resource, @Positive Integer> costs();

    /// {@inheritDoc}
    @Override
    NodeTypeId id();

}
