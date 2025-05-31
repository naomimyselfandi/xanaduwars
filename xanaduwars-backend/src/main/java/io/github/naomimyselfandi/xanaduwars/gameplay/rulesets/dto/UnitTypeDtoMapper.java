package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.gameplay.TileType;
import io.github.naomimyselfandi.xanaduwars.gameplay.TypeMapper;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

/// A MapStruct mapper for unit type DTOs.
@Mapper(uses = TypeMapper.class)
public interface UnitTypeDtoMapper extends Converter<TileType, TileTypeDto> {}
