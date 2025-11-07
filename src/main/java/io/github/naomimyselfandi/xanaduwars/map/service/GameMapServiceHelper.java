package io.github.naomimyselfandi.xanaduwars.map.service;

import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapDto;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapUpdateDto;
import io.github.naomimyselfandi.xanaduwars.map.dto.MapTileDto;
import io.github.naomimyselfandi.xanaduwars.map.dto.MapUnitDto;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.map.entity.MapTile;
import io.github.naomimyselfandi.xanaduwars.util.NonNullConverter;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import org.mapstruct.*;

@SuppressWarnings("ALL")
@AnnotateWith(NotCovered.class)
@Mapper(imports = MapUnitDto.class)
interface GameMapServiceHelper extends NonNullConverter<GameMap, GameMapDto> {

    @BeanMapping(
            unmappedSourcePolicy = ReportingPolicy.ERROR,
            unmappedTargetPolicy = ReportingPolicy.IGNORE
    )
    void update(@MappingTarget GameMap target, GameMapUpdateDto updateDto);

    // @Mapping(target = "unit.type", source = "source.unitType")
    // @Mapping(target = "unit.owner", source = "source.unitOwner")
    @Mapping(target = "unit", expression = """
            java(source.getUnitType() == null ? null : new MapUnitDto(source.getUnitType(), source.getUnitOwner()))
            """)
    MapTileDto map(MapTile source);

    @Mapping(target = "unitType", source = "source.unit.type")
    @Mapping(target = "unitOwner", source = "source.unit.owner")
    MapTile map(MapTileDto source);

}
