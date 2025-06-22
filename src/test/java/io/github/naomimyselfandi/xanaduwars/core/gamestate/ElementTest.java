package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ElementTest {

    @Mock
    private Player player0, player1;

    @Mock
    private Unit unit0, unit1;

    @ParameterizedTest
    @CsvSource(textBlock = """
            ,,false
            ,1,false
            0,,false
            0,0,true
            0,1,false
            """)
    void hasSameOwner(@Nullable Integer owner0, @Nullable Integer owner1, boolean expected) {
        var players = List.of(player0, player1);
        if (owner0 != null) {
            when(unit0.getOwner()).thenReturn(players.get(owner0));
        }
        if (owner1 != null) {
            when(unit1.getOwner()).thenReturn(players.get(owner1));
        }
        when(unit0.hasSameOwner(unit1)).thenCallRealMethod();
        assertThat(unit0.hasSameOwner(unit1)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            ,,false
            ,1,false
            0,,false
            0,0,true
            0,1,false
            """)
    void isAlly(@Nullable Integer team0, @Nullable Integer team1, boolean expected) {
        if (team0 != null) {
            when(unit0.getOwner()).thenReturn(player0);
            when(player0.getTeam()).thenReturn(new Team(team0));
        }
        if (team1 != null) {
            when(unit1.getOwner()).thenReturn(player1);
            when(player1.getTeam()).thenReturn(new Team(team1));
        }
        when(unit0.isAlly(unit1)).thenCallRealMethod();
        assertThat(unit0.isAlly(unit1)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            ,,false
            ,1,false
            0,,false
            0,0,false
            0,1,true
            """)
    void isEnemy(@Nullable Integer team0, @Nullable Integer team1, boolean expected) {
        if (team0 != null) {
            when(unit0.getOwner()).thenReturn(player0);
            when(player0.getTeam()).thenReturn(new Team(team0));
        }
        if (team1 != null) {
            when(unit1.getOwner()).thenReturn(player1);
            when(player1.getTeam()).thenReturn(new Team(team1));
        }
        when(unit0.isEnemy(unit1)).thenCallRealMethod();
        assertThat(unit0.isEnemy(unit1)).isEqualTo(expected);
    }

}
