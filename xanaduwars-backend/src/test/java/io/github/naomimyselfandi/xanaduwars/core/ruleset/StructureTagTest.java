package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTag;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class StructureTagTest {

    @Test
    void testToString(SeededRng random) {
        var tag = random.<StructureTag>get();
        assertThat(tag).hasToString(tag.label());
    }

}
