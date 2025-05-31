package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class RulesetImplTest {

    @RepeatedTest(2)
    void testToString(SeededRng random) {
        var version = random.nextVersion();
        var instance = new RulesetImpl().version(version);
        assertThat(instance).hasToString("Ruleset(%s)", version);
    }

}
