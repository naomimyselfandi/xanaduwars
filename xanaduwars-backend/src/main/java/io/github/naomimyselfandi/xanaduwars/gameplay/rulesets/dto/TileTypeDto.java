package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/// A DTO used to represent a tile type.
@Data
public class TileTypeDto {
    private TileTypeId id;
    private Name name;
    private TagSet tags;
    private Map<Tag, Double> movementTable;
    private Percent cover;
    private @Nullable Name foundation;
    private Map<Resource, Integer> costs;
    private int buildTime;
    private Map<Resource, Integer> income;
    private Set<UnitTypeId> deploymentRoster;
}
