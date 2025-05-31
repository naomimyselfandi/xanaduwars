package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class PlayerIdTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 255})
    void intValue(int intValue) {
        TestUtils.assertJson(new PlayerId(intValue), String.valueOf(intValue));
    }

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
        var intValue = random.nextIntNotNegative();
        TestUtils.assertJson(new PlayerId(intValue), "" + intValue);
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
