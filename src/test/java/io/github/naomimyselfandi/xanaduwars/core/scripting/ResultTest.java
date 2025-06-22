package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class ResultTest {

    @Test
    void okay() {
        assertThat(Result.okay()).isEqualTo(new Result.Okay());
    }

    @Test
    void fail(SeededRng random) {
        var message = random.nextString();
        assertThat(Result.fail(message)).isEqualTo(new Result.Fail(message));
    }

}
