package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class CommanderImplTest {

    @Test
    void getTags(SeededRng random) {
        assertThat(new CommanderImpl(random.get(), random.get()).getTags()).isEmpty();
    }

}
