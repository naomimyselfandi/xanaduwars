package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BiFilterOfIffTest {

    @Mock
    private Player player, anotherPlayer;

    @Mock
    private Unit foo, bar;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_Own(boolean expected) {
        when(foo.owner()).thenReturn(Optional.of(player));
        when(bar.owner()).thenReturn(Optional.of(expected ? player : anotherPlayer));
        assertThat(new BiFilterOfIff<>(BiFilterOfIff.Iff.OWN).test(foo, bar)).isEqualTo(expected);
        verify(player, never()).team();
        verify(player, never()).team();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_Ally(boolean expected) {
        when(foo.isAlly(bar)).thenReturn(expected);
        assertThat(new BiFilterOfIff<>(BiFilterOfIff.Iff.ALLY).test(foo, bar)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_Enemy(boolean expected) {
        when(foo.isAlly(bar)).thenReturn(!expected);
        assertThat(new BiFilterOfIff<>(BiFilterOfIff.Iff.ENEMY).test(foo, bar)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            OWN,own
            ALLY,ally
            ENEMY,enemy
            """)
    void testToString(BiFilterOfIff.Iff iff, String expected) {
        assertThat(new BiFilterOfIff<>(iff)).hasToString(expected);
    }

}
