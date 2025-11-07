package io.github.naomimyselfandi.xanaduwars.map.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapDto;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.security.hateoas.HateoasTest;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static io.github.naomimyselfandi.xanaduwars.security.hateoas.Faker.$;
import static io.github.naomimyselfandi.xanaduwars.map.entity.GameMap.Status.*;

@ExtendWith(SeededRandomExtension.class)
class GameMapDtoAssemblerTest extends HateoasTest {

    private GameMapDtoAssembler fixture;

    private GameMapDto dto;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new GameMapDtoAssembler();
        dto = random.get();
    }

    @Test
    void self() {
        assertQuery(dto, fixture::self, it -> it.get(dto.id()));
    }

    @Test
    void update() {
        assertQuery(dto, fixture::update, it -> it.update(dto.id(), $()));
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = "UNPUBLISHED")
    void unpublish(GameMap.Status status) {
        dto = dto.toBuilder().status(status).build();
        assertQuery(dto, fixture::unpublish, it -> it.updateStatus(dto.id(), UNPUBLISHED));
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = "PUBLISHED")
    void publish(GameMap.Status status) {
        dto = dto.toBuilder().status(status).build();
        assertQuery(dto, fixture::publish, it -> it.updateStatus(dto.id(), PUBLISHED));
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = "SUBMITTED")
    void submit(GameMap.Status status) {
        dto = dto.toBuilder().status(status).build();
        assertQuery(dto, fixture::submit, it -> it.updateStatus(dto.id(), SUBMITTED));
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = "OFFICIAL")
    void approve(GameMap.Status status) {
        dto = dto.toBuilder().status(status).build();
        assertQuery(dto, fixture::approve, it -> it.updateStatus(dto.id(), OFFICIAL));
    }

    @EnumSource
    @ParameterizedTest
    void statusMethods_WhenTheMapIsInTheAppropriateStatus_ThenNull(GameMap.Status status) {
        dto = dto.toBuilder().status(status).build();
        assertQueryIsNull(dto, switch (status) {
            case UNPUBLISHED -> fixture::unpublish;
            case PUBLISHED -> fixture::publish;
            case SUBMITTED -> fixture::submit;
            case OFFICIAL -> fixture::approve;
        });
    }

}
