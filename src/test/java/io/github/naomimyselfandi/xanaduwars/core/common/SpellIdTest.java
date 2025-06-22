package io.github.naomimyselfandi.xanaduwars.core.common;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SeededRandomExtension.class)
class SpellIdTest {

    @Test
    void json(SeededRng random) {
        var id = random.<SpellId>get();
        TestUtils.assertJson(id, String.valueOf(id.index()));
    }

}
