package io.github.naomimyselfandi.xanaduwars.map.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapDto;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapUpdateDto;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.map.service.GameMapService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.EntityModelAssembler;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameMapControllerTest {

    @Mock
    private EntityModel<GameMapDto> entityModel;

    @Mock
    private GameMapService gameMapService;

    @Mock
    private EntityModelAssembler<GameMapDto> assembler;

    @InjectMocks
    private GameMapController fixture;

    private Id<GameMap> id;

    private GameMapDto dto;

    private GameMapUpdateDto request;

    @BeforeEach
    void setup(SeededRng random) {
        id = random.get();
        dto = random.get();
        request = random.get();
    }

    @Test
    void get() {
        when(gameMapService.get(id)).thenReturn(dto);
        when(assembler.toModel(dto)).thenReturn(entityModel);
        assertThat(fixture.get(id)).isEqualTo(entityModel);
    }

    @Test
    void create() {
        when(gameMapService.create(request)).thenReturn(dto);
        when(assembler.toModel(dto)).thenReturn(entityModel);
        assertThat(fixture.create(request)).isEqualTo(entityModel);
    }

    @Test
    void update() {
        when(gameMapService.update(id, request)).thenReturn(dto);
        when(assembler.toModel(dto)).thenReturn(entityModel);
        assertThat(fixture.update(id, request)).isEqualTo(entityModel);
    }

    @EnumSource
    @ParameterizedTest
    void updateStatus(GameMap.Status status) {
        when(gameMapService.updateStatus(id, status)).thenReturn(dto);
        when(assembler.toModel(dto)).thenReturn(entityModel);
        assertThat(fixture.updateStatus(id, status)).isEqualTo(entityModel);
    }

}
