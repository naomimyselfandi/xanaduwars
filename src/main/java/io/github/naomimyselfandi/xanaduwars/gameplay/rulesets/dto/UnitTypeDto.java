package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.ext.FieldIteration;
import io.github.naomimyselfandi.xanaduwars.gameplay.Action;
import io.github.naomimyselfandi.xanaduwars.gameplay.UnitType;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.*;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/// A DTO used to represent a unit type.
@NoArgsConstructor
@FieldNameConstants(asEnum = true)
public class UnitTypeDto extends TypeDto {

    private Map<Resource, Integer> costs;
    private int vision, speed;
    private Range range;
    private Map<Name, Scalar> damageTable;
    private TagSet hangar;
    private List<Name> abilities;

    /// Mapping constructor.
    public UnitTypeDto(UnitType source) {
        super(source);
        FieldIteration.forEachField(Fields.values(), field -> switch (field) {
            case costs -> this.costs = Map.copyOf(source.costs());
            case vision -> this.vision = source.vision();
            case speed -> this.speed = source.speed();
            case range -> this.range = source.range();
            case damageTable -> this.damageTable = source
                    .damageTable()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
            case hangar -> this.hangar = source.hangar();
            case abilities -> this.abilities = source
                    .abilities()
                    .stream()
                    .map(Action::name)
                    .toList();
        });
    }

}
