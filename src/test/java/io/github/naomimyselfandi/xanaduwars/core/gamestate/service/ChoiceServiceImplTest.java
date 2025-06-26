package io.github.naomimyselfandi.xanaduwars.core.gamestate.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.CommanderId;
import io.github.naomimyselfandi.xanaduwars.core.common.SpellId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Player;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.SpellChoiceQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.InvalidOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ChoiceServiceImplTest {

    @Mock
    private Ruleset ruleset;

    @Mock
    private Commander commander;

    private CommanderId commanderId;

    @Mock
    private Spell abracadabra, alakazam;

    private SpellId abracadabraId, alakazamId;

    @Mock(answer = Answers.RETURNS_SELF)
    private Player player;

    private PlayerId playerId;

    @Mock
    private GameState gameState;

    private ChoiceServiceImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        commanderId = random.get();
        var players = random.shuffle(player, mock(), mock(), mock());
        when(gameState.getPlayers()).thenReturn(players);
        playerId = new PlayerId(players.indexOf(player));
        abracadabraId = random.get();
        alakazamId = random.not(abracadabraId);
        fixture = new ChoiceServiceImpl();
    }

    @Test
    void getChoices() {
        when(player.getCommander()).thenReturn(Optional.of(commander));
        when(commander.getId()).thenReturn(commanderId);
        when(player.getChosenSpells()).thenReturn(List.of(abracadabra, alakazam));
        when(abracadabra.getId()).thenReturn(abracadabraId);
        when(alakazam.getId()).thenReturn(alakazamId);
        var expected = new ChoiceDto().setCommander(commanderId).setSpells(List.of(abracadabraId, alakazamId));
        assertThat(fixture.getChoices(gameState, playerId)).isEqualTo(expected);
    }

    @Test
    void getChoices_ToleratesMissingCommander() {
        assertThat(fixture.getChoices(gameState, playerId)).isEqualTo(new ChoiceDto().setSpells(List.of()));
    }

    @Test
    void setChoices() {
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(ruleset.getCommander(commanderId)).thenReturn(commander);
        when(ruleset.getSpell(abracadabraId)).thenReturn(abracadabra);
        when(ruleset.getSpell(alakazamId)).thenReturn(alakazam);
        var query = new SpellChoiceQuery(commander, List.of(abracadabra, alakazam));
        when(gameState.evaluate(query)).thenReturn(Result.okay());
        var choices = new ChoiceDto().setCommander(commanderId).setSpells(List.of(abracadabraId, alakazamId));
        assertThatCode(() -> fixture.setChoices(gameState, playerId, choices)).doesNotThrowAnyException();
        verify(player).setCommander(commander);
        verify(player).setChosenSpells(List.of(abracadabra, alakazam));
    }

    @Test
    void setChoices_WhenTheChoicesAreRejected_ThenThrows(SeededRng random) {
        var fail = random.<Result.Fail>get();
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(ruleset.getCommander(commanderId)).thenReturn(commander);
        when(ruleset.getSpell(abracadabraId)).thenReturn(abracadabra);
        when(ruleset.getSpell(alakazamId)).thenReturn(alakazam);
        var query = new SpellChoiceQuery(commander, List.of(abracadabra, alakazam));
        when(gameState.evaluate(query)).thenReturn(fail);
        var choices = new ChoiceDto().setCommander(commanderId).setSpells(List.of(abracadabraId, alakazamId));
        assertThatThrownBy(() -> fixture.setChoices(gameState, playerId, choices))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage(fail.message());
        verify(player, never()).setCommander(any());
        verify(player, never()).setCommander(isNull());
        verify(player, never()).setChosenSpells(any());
    }

    @Test
    void setChoice_WhenNoCommanderIsSet_ThenThrows() {
        assertThatThrownBy(() -> fixture.setChoices(gameState, playerId, new ChoiceDto()))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Must choose a commander.");
        verify(player, never()).setCommander(any());
        verify(player, never()).setCommander(isNull());
        verify(player, never()).setChosenSpells(any());
    }

    @Test
    void setChoices_WhenASpellIsNotDefined_ThenThrows() {
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(ruleset.getCommander(commanderId)).thenReturn(commander);
        when(ruleset.getSpell(abracadabraId)).thenReturn(abracadabra);
        when(ruleset.getSpell(alakazamId)).thenThrow(IndexOutOfBoundsException.class);
        var choices = new ChoiceDto().setCommander(commanderId).setSpells(List.of(abracadabraId, alakazamId));
        assertThatThrownBy(() -> fixture.setChoices(gameState, playerId, choices))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Unknown spell.");
        verify(player, never()).setCommander(any());
        verify(player, never()).setCommander(isNull());
        verify(player, never()).setChosenSpells(any());
    }

    @Test
    void setChoices_WhenTheCommanderIsNotDefined_ThenThrows() {
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(ruleset.getCommander(commanderId)).thenThrow(IndexOutOfBoundsException.class);
        var choices = new ChoiceDto().setCommander(commanderId).setSpells(List.of(abracadabraId, alakazamId));
        assertThatThrownBy(() -> fixture.setChoices(gameState, playerId, choices))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Unknown commander.");
        verify(player, never()).setCommander(any());
        verify(player, never()).setCommander(isNull());
        verify(player, never()).setChosenSpells(any());
    }

}
