package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.TileTag;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitTag;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(SeededRandomExtension.class)
class TargetDeserializerTest {

    @MethodSource
    @ParameterizedTest
    void deserialize(String text, Target<?> expected) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(text);
        assertThat(objectMapper.readValue(json, Target.class)).isEqualTo(expected);
    }

    @Test
    void deserialize_Invalid(SeededRng random) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var string = random.nextString();
        var json = objectMapper.writeValueAsString("unit." + string);
        var format = "Unexpected input in target specification 'unit.%s': '.%s'.";
        assertThatThrownBy(() -> objectMapper.readValue(json, Target.class))
                .isInstanceOf(ValueInstantiationException.class)
                .hasMessageStartingWith(format, string, string);
    }

    private static Stream<Arguments> deserialize() {
        var tile = TargetOfTile.TILE;
        var unit = new TargetOfUnit(tile);
        var ally = new TargetOfAllyUnit(unit);
        var enemy = new TargetOfEnemyUnit(unit);
        var soldier = new TargetOfUnitTag(unit, List.of(new UnitTag("Soldier")));
        var vehicle = new TargetOfUnitTag(unit, List.of(new UnitTag("Treaded"), new UnitTag("Wheeled")));
        var visible = new TargetOfVision<>(tile);
        var water = new TargetOfTileTag(tile, List.of(new TileTag("Water")));
        return Stream.of(
                arguments("", TargetOfNothing.NOTHING),
                arguments("unit", unit),
                arguments("unit.ally", ally),
                arguments("unit.enemy", enemy),
                arguments("unit[1,4]", new TargetOfRange<>(unit, 1, 4)),
                arguments("unit[Soldier]", soldier),
                arguments("unit[Treaded|Wheeled]", vehicle),
                arguments("unit[2,4].ally", new TargetOfRange<>(ally, 2, 4)),
                arguments("unit[1,3].enemy", new TargetOfRange<>(enemy, 1, 3)),
                arguments(
                        "unit[0,1].ally[Treaded|Wheeled]",
                        new TargetOfRange<>(new TargetOfAllyUnit(vehicle), 0, 1)
                ),
                arguments("tile", tile),
                arguments("tile.visible", visible),
                arguments("tile[Water]", water),
                arguments("tile[2,3].visible", new TargetOfRange<>(visible, 2, 3)),
                arguments("unit.tile[Water]", new TargetOfUnit(water))
        );
    }

}
