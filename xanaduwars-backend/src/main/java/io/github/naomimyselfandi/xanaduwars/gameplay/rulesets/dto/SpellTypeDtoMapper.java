package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.gameplay.SpellType;
import io.github.naomimyselfandi.xanaduwars.gameplay.TypeMapper;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

/// A MapStruct mapper for spell type DTOs.
@Mapper(uses = TypeMapper.class)
public interface SpellTypeDtoMapper extends Converter<SpellType<?>, SpellTypeDto> {}
