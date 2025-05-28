package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.UnitStat;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.UnitStatQuery;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitStatRuleTest {

    @Mock
    private Unit unit;

    @Mock
    private Filter<Unit> filter;

    private UnitStatRule fixture;

    @BeforeEach
    void setup(SeededRng random) {
        var stat = random.pick(UnitStat.values());
        var amount = random.nextInt(1, 10);
        fixture = new UnitStatRule(filter, stat, amount);
    }

    @RepeatedTest(3)
    void handle(SeededRng random) {
        var value = random.nextInt(1, 10);
        var query = new UnitStatQuery(unit, fixture.stat());
        assertThat(fixture.handle(query, value)).isEqualTo(value + fixture.amount());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void handles(boolean passesFilter, SeededRng random) {
        when(filter.test(unit)).thenReturn(passesFilter);
        for (var stat : UnitStat.values()) {
            var query = new UnitStatQuery(unit, stat);
            var expected = passesFilter && (stat == fixture.stat());
            assertThat(fixture.handles(query, random.nextInt(1, 10))).isEqualTo(expected);
        }
    }

}
