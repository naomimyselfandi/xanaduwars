package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class FirepowerQueryTest {

    @Test
    void defaultValue(SeededRng random) {
        assertThat(random.<FirepowerQuery>get().defaultValue()).isOne();
    }

}
