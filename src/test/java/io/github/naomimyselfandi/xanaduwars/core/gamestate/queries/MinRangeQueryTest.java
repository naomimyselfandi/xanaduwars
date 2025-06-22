package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class MinRangeQueryTest {

    @Test
    void defaultValue(SeededRng random) {
        var query = random.<MinRangeQuery>get();
        assertThat(query.defaultValue()).isEqualTo(query.targetSpec().minRange());
    }

}
