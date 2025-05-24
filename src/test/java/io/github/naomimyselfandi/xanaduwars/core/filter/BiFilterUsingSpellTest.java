package io.github.naomimyselfandi.xanaduwars.core.filter;

import io.github.naomimyselfandi.xanaduwars.core.Player;
import io.github.naomimyselfandi.xanaduwars.core.Spell;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    @CsvSource(textBlock = """
            true,true,true
            true,false,true
            false,true,true
            false,false,false
            """)
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
