package io.github.naomimyselfandi.xanaduwars.core.model;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class WeaponTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0,1,1
            0,1,1,1
            2,3,2,3
            """)
    void normalization(int givenMinRange, int givenMaxRange, int minRange, int maxRange, SeededRng random) {
        var name = random.nextString();
        var damage = new HashMap<UnitTag, Integer>();
        damage.put(random.get(), random.get());
        damage.put(random.get(), random.get());
        var weapon = new Weapon(name, damage, givenMinRange, givenMaxRange);
        var normalized = new Weapon(name, damage, minRange, maxRange);
        assertThat(weapon).isEqualTo(normalized);
        assertThat(weapon.damage()).isUnmodifiable();
    }

}
