package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class PlayerIdTest {

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(new PlayerId(0), new PlayerId(1), new PlayerId(2));
    }

    @RepeatedTest(2)
    void testToString(SeededRng random) {
        var i = random.nextInt(255);
        assertThat(new PlayerId(i)).hasToString("Player(%d)", i);
    }

    @Test
    void json(SeededRng random) {
        var playerId = random.nextPlayerId();
        TestUtils.assertJson(playerId, "" + playerId.intValue());
    }

    @Test
    void json_AsMapKey(SeededRng random) {
        var foo = random.nextInt();
        var bar = random.nextInt();
        TestUtils.assertJson(
                new TypeReference<Map<PlayerId, Integer>>() {},
                Map.of(new PlayerId(foo), bar),
                "{\"%d\":%d}".formatted(foo, bar)
        );
    }

}
