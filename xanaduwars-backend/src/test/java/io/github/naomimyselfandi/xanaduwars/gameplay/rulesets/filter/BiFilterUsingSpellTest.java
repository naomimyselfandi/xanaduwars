package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Spell;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BiFilterUsingSpellTest {

    @Mock
    private Spell foo, bar;

    @Mock
    private Player player;

    @Mock
    private Unit unit;

    @Mock
    private BiFilter<Unit, Spell> filter;

    @InjectMocks
    private BiFilterUsingSpell<Unit> fixture;

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void test(boolean acceptsFirstSpell, boolean acceptsSecondSpell, boolean expected) {
        when(player.activeSpells()).thenReturn(List.of(foo, bar));
        when(filter.test(unit, foo)).thenReturn(acceptsFirstSpell);
        lenient().when(filter.test(unit, bar)).thenReturn(acceptsSecondSpell);
        assertThat(fixture.test(unit, player)).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("spell.filter");
    }

}
