package io.github.naomimyselfandi.xanaduwars.core.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.messages.AreSpellChoicesOkQuery;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SpellChoiceServiceImplTest {

    @Mock
    private Version version;

    @Mock
    private GameState gameState;

    @Mock
    private Player player;

    @Mock
    private Commander commander;

    @Mock
    private Ability foo, bar, baz;

    private SpellChoiceServiceImpl fixture;

    @BeforeEach
    void setup() {
        fixture = new SpellChoiceServiceImpl();
    }

    @Test
    void select() {
        when(player.getGameState()).thenReturn(gameState);
        when(commander.getSignatureSpells()).thenReturn(List.of(foo));
        when(gameState.evaluate(new AreSpellChoicesOkQuery(commander, List.of(bar, baz)))).thenReturn(true);
        assertThatCode(() -> fixture.select(player, commander, List.of(bar, baz))).doesNotThrowAnyException();
        verify(player).setCommander(commander);
        verify(player).setAbilities(List.of(foo, bar, baz));
    }

    @Test
    void select_WhenTheChoicesAreInvalid_ThenThrows(SeededRng random) {
        var name = random.nextString();
        when(player.getGameState()).thenReturn(gameState);
        when(commander.getName()).thenReturn(name);
        when(gameState.evaluate(new AreSpellChoicesOkQuery(commander, List.of(bar, baz)))).thenReturn(false);
        assertThatThrownBy(() -> fixture.select(player, commander, List.of(bar, baz)))
                .isInstanceOf(SpellChoiceException.class)
                .hasMessage("Illegal spell choices for '%s'.", name);
        verify(player, never()).setCommander(any());
        verify(player, never()).setAbilities(any());
    }

    @Test
    void getSuggestions() {
        when(gameState.getVersion()).thenReturn(version);
        when(version.getDeclarations()).then(_ -> Stream.of(new Object(), foo, bar, baz));
        when(gameState.evaluate(new AreSpellChoicesOkQuery(commander, List.of(foo)))).thenReturn(true);
        when(gameState.evaluate(new AreSpellChoicesOkQuery(commander, List.of(bar)))).thenReturn(true);
        when(gameState.evaluate(new AreSpellChoicesOkQuery(commander, List.of(baz)))).thenReturn(true);
        when(gameState.evaluate(new AreSpellChoicesOkQuery(commander, List.of(foo, foo)))).thenReturn(false);
        when(gameState.evaluate(new AreSpellChoicesOkQuery(commander, List.of(foo, bar)))).thenReturn(true);
        when(gameState.evaluate(new AreSpellChoicesOkQuery(commander, List.of(foo, baz)))).thenReturn(false);
        assertThat(fixture.getSuggestions(gameState, commander, List.of())).containsExactly(foo, bar, baz);
        assertThat(fixture.getSuggestions(gameState, commander, List.of(foo))).containsExactly(bar);
    }

}
