package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

/// A MapStruct mapper for ruleset DTOs.
@Mapper(uses = TypeMapper.class)
public interface RulesetDtoMapper extends Converter<Ruleset, RulesetDto> {}
