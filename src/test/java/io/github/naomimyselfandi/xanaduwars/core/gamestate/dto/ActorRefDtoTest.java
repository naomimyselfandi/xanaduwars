package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(SeededRandomExtension.class)
class ActorRefDtoTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void json_Player(int playerId) {
        TestUtils.assertJson(ActorRefDto.class, new PlayerRefDto(playerId), """
           {"player": %d}
           """.formatted(playerId));
    }

    @EnumSource
    @ParameterizedTest
    void json_Physical(PhysicalRefDto.Kind kind, SeededRng random) {
        var x = random.nextInt();
        var y = random.nextInt();
        TestUtils.assertJson(ActorRefDto.class, new PhysicalRefDto(kind, x, y), """
            {"kind": "%s", "x": %d, "y": %d}
            """.formatted(switch (kind) {
            case STRUCTURE -> "Structure";
            case TILE -> "Tile";
            case UNIT -> "Unit";
        }, x, y));
    }

}
