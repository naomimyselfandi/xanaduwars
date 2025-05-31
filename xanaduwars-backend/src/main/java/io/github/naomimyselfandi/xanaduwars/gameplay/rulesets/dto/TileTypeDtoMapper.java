package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.gameplay.TileType;
import io.github.naomimyselfandi.xanaduwars.gameplay.TypeMapper;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

/// A MapStruct mapper for commander DTOs.
@Mapper(uses = TypeMapper.class)
public interface TileTypeDtoMapper extends Converter<TileType, TileTypeDto> {}
