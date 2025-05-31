package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class CommanderImplTest {

    @Test
    void testToString(SeededRng random) {
        var index = random.nextCommanderId();
        var name = random.nextName();
        assertThat(new CommanderImpl(index, name)).hasToString("%s(%s)", index, name);
    }

}
