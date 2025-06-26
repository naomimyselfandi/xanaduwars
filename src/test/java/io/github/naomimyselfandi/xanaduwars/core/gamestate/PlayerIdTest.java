package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;

class PlayerIdTest {

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(new PlayerId(0), new PlayerId(1), new PlayerId(2));
    }

}
