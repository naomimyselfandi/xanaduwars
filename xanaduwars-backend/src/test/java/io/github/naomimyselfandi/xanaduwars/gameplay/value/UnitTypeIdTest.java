package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class UnitTypeIdTest {

    @Test
    void testToString(SeededRng random) {
        var id = random.nextUnitTypeId();
        assertThat(id).hasToString("UnitType[%d]", id.index());
    }

}
