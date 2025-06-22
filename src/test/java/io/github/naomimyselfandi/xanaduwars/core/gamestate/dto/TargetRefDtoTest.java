package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Direction;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

@ExtendWith(SeededRandomExtension.class)
class TargetRefDtoTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            NORTH,EAST,NORTH,N,E,N
            WEST,WEST,SOUTH,W,W,S
            """)
    void json_Path(Direction d1, Direction d2, Direction d3, String s1, String s2, String s3) {
        TestUtils.assertJson(TargetRefDto.class, new PathRefDto(List.of(d1, d2, d3)), """
            {"path": ["%s", "%s", "%s"]}
            """.formatted(s1, s2, s3));
    }

    @EnumSource
    @ParameterizedTest
    void json_Physical(PhysicalRefDto.Kind kind, SeededRng random) {
        var x = random.nextInt();
        var y = random.nextInt();
        TestUtils.assertJson(TargetRefDto.class, new PhysicalRefDto(kind, x, y), """
            {"kind": "%s", "x": %d, "y": %d}
            """.formatted(switch (kind) {
            case STRUCTURE -> "Structure";
            case TILE -> "Tile";
            case UNIT -> "Unit";
        }, x, y));
    }

}
