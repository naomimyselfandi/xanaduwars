package io.github.naomimyselfandi.xanaduwars.core.filter;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.Player;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class BiFilterOfIffTest {

    @Mock
    private Player self, ally, enemy;

    @Mock
    private Unit foo, bar;

    @BeforeEach
    void setup(SeededRandom random) {
        int allyTeam, enemyTeam;
        do {
            allyTeam = random.nextInt(Integer.MAX_VALUE);
            enemyTeam = random.nextInt(Integer.MAX_VALUE);
        } while (allyTeam == enemyTeam);
        when(self.team()).thenReturn(allyTeam);
        when(ally.team()).thenReturn(allyTeam);
        when(enemy.team()).thenReturn(enemyTeam);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            OWN,OWN,true
            OWN,ALLY,false
            OWN,ENEMY,false
            OWN,,false
            ALLY,OWN,true
            ALLY,ALLY,true
            ALLY,ENEMY,false
            ALLY,,false
            ENEMY,OWN,false
            ENEMY,ALLY,false
            ENEMY,ENEMY,true
            ENEMY,,false
            """)
    void test(BiFilterOfIff.Iff iff, @Nullable BiFilterOfIff.Iff otherPlayer, boolean expected) {
        var filter = new BiFilterOfIff<>(iff);
        when(foo.owner()).thenReturn(Optional.of(self));
        when(bar.owner()).thenReturn(player(otherPlayer));
        assertThat(filter.test(bar, foo)).isEqualTo(expected);
        when(bar.owner()).thenReturn(Optional.of(self));
        when(foo.owner()).thenReturn(player(otherPlayer));
        assertThat(filter.test(bar, foo)).isEqualTo(expected);
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

    private Optional<Player> player(@Nullable BiFilterOfIff.Iff iff) {
        return Optional.ofNullable(switch (iff) {
            case OWN -> self;
            case ALLY -> ally;
            case ENEMY -> enemy;
            case null -> null;
        });
    }

}
