package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitTypeImplTest {

    @Test
    void testToString(SeededRng random) {
        var id = random.nextUnitTypeId();
        var name = random.nextName();
        assertThat(new UnitTypeImpl(id, name)).hasToString("%s(%s)", id, name);
    }

}
