package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class CommanderIdTest {

    @Test
    void testToString(SeededRng random) {
        var id = random.nextCommanderId();
        assertThat(id).hasToString("Commander[%d]", id.index());
    }

}
