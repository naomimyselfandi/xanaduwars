package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ActionValidationTest {

    @Mock
    private Player activePlayer, inactivePlayer;

    @Mock
    private GameState gameState;

    @Mock
    private Tile tile;

    @Mock
    private Unit unit;

    @BeforeEach
    void setup() {
        when(gameState.activePlayer()).thenReturn(activePlayer);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            PLAYER,true
            TILE,true
            UNIT,true
            ACTED_UNIT,false
            """)
    void defaultValue(String kind, boolean expected) {
        var element = element(kind);
        when(element.owner()).thenReturn(Optional.of(activePlayer));
        assertThat(new ActionValidation(element).defaultValue()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PLAYER", "TILE", "UNIT", "ACTED_UNIT"})
    void defaultValue_WhenTheSubjectIsOwnedByAnInactivePlayer_ThenFalse(String kind) {
        var element = element(kind);
        when(element.owner()).thenReturn(Optional.of(inactivePlayer));
        assertThat(new ActionValidation(element).defaultValue()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PLAYER", "TILE", "UNIT", "ACTED_UNIT"})
    void defaultValue_WhenTheSubjectIsNotOwnedByAnyPlayer_ThenFalse(String kind) {
        var element = element(kind);
        when(element.owner()).thenReturn(Optional.empty());
        assertThat(new ActionValidation(element).defaultValue()).isFalse();
    }

    private Element element(String kind) {
        var element = switch (kind) {
            case "PLAYER" -> activePlayer;
            case "TILE" -> tile;
            case "UNIT" -> {
                when(unit.canAct()).thenReturn(true);
                yield unit;
            }
            case "ACTED_UNIT" -> {
                when(unit.canAct()).thenReturn(false);
                yield unit;
            }
            default -> Assertions.<Element>fail();
        };
        when(element.gameState()).thenReturn(gameState);
        return element;
    }

}
