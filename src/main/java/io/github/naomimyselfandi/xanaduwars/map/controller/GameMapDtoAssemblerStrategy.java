package io.github.naomimyselfandi.xanaduwars.map.controller;

import io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapDto;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapModel;
import io.github.naomimyselfandi.xanaduwars.security.hateoas.EntityModelStrategy;
import io.github.naomimyselfandi.xanaduwars.util.EntityModelAssembler;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
@NotCovered
@SuppressWarnings("ALL")
abstract class GameMapDtoAssemblerStrategy implements EntityModelStrategy<GameMapDto> {

    @Autowired
    protected EntityModelAssembler<BaseAccountDto> baseAccountDtoAssembler;

    @Override
    @Mapping(target = "author", expression = "java(baseAccountDtoAssembler.toModel(source.author()))")
    public abstract GameMapModel createModel(GameMapDto source);

}
