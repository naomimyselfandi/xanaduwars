package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class TeamTest {

    private Team fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = random.get();
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString(String.valueOf(fixture.team()));
    }

    @Test
    void json() {
        TestUtils.assertJson(fixture, String.valueOf(fixture.team()));
    }

}
