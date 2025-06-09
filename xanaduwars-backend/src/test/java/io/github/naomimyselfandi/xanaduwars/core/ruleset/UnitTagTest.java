package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitTag;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class UnitTagTest {

    @Test
    void testToString(SeededRng random) {
        var tag = random.<UnitTag>get();
        assertThat(tag).hasToString(tag.label());
    }

}
