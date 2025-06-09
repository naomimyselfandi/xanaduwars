package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class HangarTest {

    private UnitTag foo, bar, spam, eggs;

    private Hangar hangar;

    @BeforeEach
    void setup(SeededRng random) {
        foo = random.get();
        bar = random.get(foo);
        spam = random.get(foo, bar);
        eggs = random.get(foo, bar, spam);
        hangar = new Hangar(Set.of(foo, bar));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void supports(boolean value, SeededRng random) {
        var a = random.nextBoolean() ? spam : eggs;
        var b = random.nextBoolean() ? foo : bar;
        assertThat(hangar.supports(value ? Set.of(a, b) : Set.of(a))).isEqualTo(value);
    }

}
