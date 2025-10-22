package io.github.naomimyselfandi.xanaduwars.util;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class IdTest {

    @Test
    void testToString(SeededRng random) {
        var id = random.<Id<Void>>get();
        assertThat(id).hasToString(id.id().toString());
    }

}
