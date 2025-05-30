package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.ext.FieldIteration;
import io.github.naomimyselfandi.xanaduwars.gameplay.TileType;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Tag;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/// A DTO used to represent a tile type.
@NoArgsConstructor
@FieldNameConstants(asEnum = true)
public class TileTypeDto extends TypeDto {

    private Map<Tag, Double> movementTable;
    private Percent cover;
    private @Nullable TileTypeDto foundation;
    private Map<Resource, Integer> costs, income;
    private int buildTime;
    private List<UnitTypeDto> deploymentRoster;

    /// Mapping constructor.
    public TileTypeDto(TileType source) {
        super(source);
        FieldIteration.forEachField(Fields.values(), field -> switch (field) {
            case movementTable -> this.movementTable = Map.copyOf(source.movementTable());
            case cover -> this.cover = source.cover();
            case foundation -> this.foundation = source.foundation().map(TileTypeDto::new).orElse(null);
            case costs -> this.costs = Map.copyOf(source.costs());
            case income -> this.income = Map.copyOf(source.income());
            case buildTime -> this.buildTime = source.buildTime();
            case deploymentRoster -> this.deploymentRoster = source
                    .deploymentRoster()
                    .stream()
                    .sorted(Comparator.comparing(unitType -> unitType.id().index()))
                    .map(UnitTypeDto::new)
                    .toList();
        });
    }

}
