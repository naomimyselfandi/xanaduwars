package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class MovementTableTest {

    private UnitTag foo, bar;
    private double fooCost, barCost;

    private MovementTable fixture;

    @BeforeEach
    void setup(SeededRng random) {
        foo = random.get();
        fooCost = random.get();
        bar = random.get(foo);
        barCost = random.get(fooCost);
        fixture = new MovementTable(Map.of(foo, fooCost, bar, barCost));
    }

    @Test
    void cost() {
        assertThat(fixture.cost(List.of(foo))).isEqualTo(fooCost);
        assertThat(fixture.cost(List.of(bar))).isEqualTo(barCost);
    }

    @Test
    void cost_WhenMultipleCostsArePresent_ThenReturnsTheMinimum() {
        var min = Math.min(fooCost, barCost);
        assertThat(fixture.cost(List.of(foo, bar))).isEqualTo(min);
        assertThat(fixture.cost(List.of(bar, foo))).isEqualTo(min);
    }

    @Test
    void cost_WhenNoCostIsPresent_ThenReturnsNaN(SeededRng random) {
        var anotherTag = random.get(foo, bar);
        assertThat(fixture.cost(List.of(anotherTag))).isNaN();
    }

}
