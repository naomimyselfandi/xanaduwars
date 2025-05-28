package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionValidation;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class PreActionRuleTest {

    @Mock
    private GameState gameState;

    @Mock
    private Player player, anotherPlayer;

    @Mock
    private Spell spell;

    @Mock
    private Tile tile;

    @Mock
    private Unit unit;

    @InjectMocks
    private PreActionRule fixture;

    @ParameterizedTest
    @CsvSource(textBlock = """
            PLAYER,false
            PLAYER,true
            SPELL,false
            SPELL,true
            TILE,false
            TILE,true
            UNIT,false
            UNIT,true
            """)
    void isValid(String kind, boolean expected) {
        var subject = switch (kind) {
            case "PLAYER" -> player;
            case "SPELL" -> spell;
            case "TILE" -> tile;
            case "UNIT" -> {
                when(unit.tile()).thenReturn(Optional.of(tile));
                yield unit;
            }
            default -> Assertions.<Element>fail();
        };
        if (subject == unit) when(unit.canAct()).thenReturn(true);
        when(subject.owner()).thenReturn(Optional.of(player));
        when(subject.gameState()).thenReturn(gameState);
        when(gameState.activePlayer()).thenReturn(expected ? player : anotherPlayer);
        assertThat(fixture.isValid(new ActionValidation(subject))).isEqualTo(expected);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void test_WhenTheSubjectIsAUnit_ThenChecksItsState(boolean canAct, boolean onTile, boolean expected) {
        when(unit.owner()).thenReturn(Optional.of(player));
        when(unit.gameState()).thenReturn(gameState);
        when(gameState.activePlayer()).thenReturn(player);
        when(unit.canAct()).thenReturn(canAct);
        when(unit.tile()).thenReturn(Optional.of(tile).filter(_ -> onTile));
        assertThat(fixture.isValid(new ActionValidation(unit))).isEqualTo(expected);
    }

}
