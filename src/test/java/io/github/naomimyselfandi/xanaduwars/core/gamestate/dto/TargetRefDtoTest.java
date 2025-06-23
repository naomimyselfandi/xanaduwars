package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Direction;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(SeededRandomExtension.class)
class TargetRefDtoTest {

    @MethodSource
    @ParameterizedTest
    void json(TargetRefDto dto, @Language("json") String json) {
        TestUtils.assertJson(TargetRefDto.class, dto, json);
    }

    private static Stream<Arguments> json() {
        return Stream.of(
                arguments(
                        new StructureReferenceDto(1, 2),
                        """
                        {"structureX": 1, "structureY": 2}
                        """
                ),
                arguments(
                        new TileReferenceDto(3, 4),
                        """
                        {"tileX": 3, "tileY": 4}
                        """
                ),
                arguments(
                        new UnitReferenceDto(5, 6),
                        """
                        {"unitX": 5, "unitY": 6}
                        """
                ),
                arguments(
                        new PathRefDto(List.of(Direction.EAST, Direction.NORTH, Direction.EAST)),
                        """
                        {"path": ["E", "N", "E"]}
                        """
                )
        );
    }

}
