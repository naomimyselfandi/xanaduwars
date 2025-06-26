package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStateDtoConverterTest {

    @Mock
    private Tile tile00, tile10, tile01, tile11;

    private TileDto dto00, dto10, dto01, dto11;

    @Mock
    private Structure structure;

    private StructureDto structureDto;

    @Mock
    private Unit unit;

    private UnitDto unitDto;

    private GameStateDtoConverter fixture;

    @BeforeEach
    void setup(SeededRng random) {
        when(tile00.getId()).thenReturn(new TileId(0, 0));
        when(tile01.getId()).thenReturn(new TileId(0, 1));
        when(tile10.getId()).thenReturn(new TileId(1, 0));
        when(tile11.getId()).thenReturn(new TileId(1, 1));
        dto00 = new TileDto().setId(new TileId(0, 0));
        dto01 = new TileDto().setId(new TileId(0, 1));
        dto10 = new TileDto().setId(new TileId(1, 0));
        dto11 = new TileDto().setId(new TileId(1, 1));
        var map = Map.of(tile00, dto00, tile01, dto01, tile10, dto10, tile11, dto11);
        structureDto = random.get();
        unitDto = random.get();
        fixture = new GameStateDtoConverter() {

            @Override
            public GameStateDto convert(GameState source) {
                throw new UnsupportedOperationException();
            }

            @Override
            public PlayerDto convert(Player source) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SpellSlotDto convert(SpellSlot source) {
                throw new UnsupportedOperationException();
            }

            @Override
            public StructureDto convert(Structure source) {
                assertThat(source).isEqualTo(structure);
                return structureDto;
            }

            @Override
            public TileDto convert(Tile source) {
                return map.get(source);
            }

            @Override
            public UnitDto convert(Unit source) {
                assertThat(source).isEqualTo(unit);
                return unitDto;
            }

        };
    }

    @Test
    void convert() {
        var source = new TreeMap<>(Map.of(
                tile00.getId(), tile00,
                tile01.getId(), tile01,
                tile10.getId(), tile10,
                tile11.getId(), tile11
        ));
        assertThat(fixture.convert(source)).containsExactly(
                List.of(dto00, dto10),
                List.of(dto01, dto11)
        );
    }

    @Test
    void convertOptionalUnit() {
        assertThat(fixture.convertOptionalUnit(Optional.empty())).isNull();
        assertThat(fixture.convertOptionalUnit(Optional.of(unit))).isEqualTo(unitDto);
    }

    @Test
    void convertOptionalStructure() {
        assertThat(fixture.convertOptionalStructure(Optional.empty())).isNull();
        assertThat(fixture.convertOptionalStructure(Optional.of(structure))).isEqualTo(structureDto);
    }

}
