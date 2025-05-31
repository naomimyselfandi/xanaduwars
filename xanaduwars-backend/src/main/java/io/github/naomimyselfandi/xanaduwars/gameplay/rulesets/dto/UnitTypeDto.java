package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

/// A DTO used to represent a unit type.
@Data
public class UnitTypeDto {
    private UnitTypeId id;
    private Name name;
    private TagSet tags;
    private Map<Resource, Integer> costs;
    private int vision;
    private int speed;
    private Range range;
    private Map<Name, Scalar> damageTable;
    private TagSet hangar;
    private List<Name> abilities;
}
