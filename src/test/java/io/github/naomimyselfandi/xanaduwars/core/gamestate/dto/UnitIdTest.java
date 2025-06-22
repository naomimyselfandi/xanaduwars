package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.UnitId;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;

class UnitIdTest {

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(new UnitId(0), new UnitId(1), new UnitId(2));
    }

}
