package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.message.EventListener;
import io.github.naomimyselfandi.xanaduwars.core.message.Rule;
import io.github.naomimyselfandi.xanaduwars.core.messages.TurnStartedEvent;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.script.Function;
import io.github.naomimyselfandi.xanaduwars.core.script.Library;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("resource")
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStateImplTest {

    @Mock
    private Ability ability1, ability2;

    @Mock
    private Library library;

    @Mock
    private Function function;

    @Mock
    private EventListener eventListener;

    @Mock
    private Script script;

    @Mock
    private Version version;

    @Mock
    private Player player;

    @Mock
    private Player player1, player2;

    @Mock
    private Tile tile1, tile2, tile3, tile4, tile5, tile6;

    @Mock
    private Unit unit1, unit2, unit3;

    private GameStateImpl fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        this.random = random;
        var players = List.of(player1, player2);
        var tiles = List.of(tile1, tile2, tile3, tile4, tile5, tile6);
        fixture = new GameStateImpl(version, false, 3, 2, players, tiles, 42);
        fixture.attachEventListener(eventListener);
    }

    @Test
    void getTurn() {
        assertThat(fixture.getTurn()).isEqualTo(42);
    }

    @Test
    void setTurn() {
        doAnswer(invocation -> {
            var event = invocation.<TurnStartedEvent>getArgument(0);
            var actor = event.actor();
            verify(actor).setActiveAbilities(List.of());
            return null;
        }).when(eventListener).receive(any(TurnStartedEvent.class));
        when(player2.getUnits()).then(_ -> Stream.of(unit1));
        assertThat(fixture.setTurn(43)).isSameAs(fixture);
        var inOrder = inOrder(eventListener);
        inOrder.verify(eventListener).receive(new TurnStartedEvent(player2));
        inOrder.verify(eventListener).receive(new TurnStartedEvent(unit1));
        assertThat(fixture.getActivePlayer()).isEqualTo(player2);
    }

    @Test
    void getActivePlayer() {
        assertThat(fixture.getActivePlayer()).isEqualTo(player1);
    }

    @Test
    void getPlayer() {
        assertThat(fixture.getPlayer(0)).isEqualTo(player1);
        assertThat(fixture.getPlayer(1)).isEqualTo(player2);
    }

    @Test
    void getTile() {
        assertThat(fixture.getTile(0, 0)).isEqualTo(tile1);
        assertThat(fixture.getTile(1, 0)).isEqualTo(tile2);
        assertThat(fixture.getTile(2, 0)).isEqualTo(tile3);
        assertThat(fixture.getTile(0, 1)).isEqualTo(tile4);
        assertThat(fixture.getTile(1, 1)).isEqualTo(tile5);
        assertThat(fixture.getTile(2, 1)).isEqualTo(tile6);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            -1,0
            0,-1
            0,2
            3,0
            """)
    void getTile_WhenTheCoordinatesAreOutOfBounds_ThenNull(int x, int y) {
        assertThat(fixture.getTile(x, y)).isNull();
    }

    @Test
    void getUnits() {
        when(tile1.getUnit()).thenReturn(unit1);
        when(tile6.getUnit()).thenReturn(unit2);
        when(unit2.getUnit()).thenReturn(unit3);
        assertThat(fixture.getUnits()).containsExactly(unit1, unit2, unit3);
    }

    @Test
    void getVersion() {
        assertThat(fixture.getVersion()).isEqualTo(version);
    }

    @Test
    void getWidth() {
        assertThat(fixture.getWidth()).isEqualTo(3);
    }

    @Test
    void getHeight() {
        assertThat(fixture.getHeight()).isEqualTo(2);
    }

    @Test
    void getPlayers() {
        assertThat(fixture.getPlayers()).isUnmodifiable().containsExactly(player1, player2);
    }

    @Test
    void getTiles() {
        assertThat(fixture.getTiles()).isUnmodifiable().containsExactly(tile1, tile2, tile3, tile4, tile5, tile6);
    }

    @Test
    void lookup() {
        var name = random.nextString();
        var value = new Object();
        when(version.lookup(name)).thenReturn(value);
        assertThat(fixture.lookup(name)).isEqualTo(value);
    }

    @Test
    void lookup_WhenTheLookupReturnsAScript_ThenExecutesIt() {
        var name = random.nextString();
        when(version.lookup(name)).thenReturn(script);
        when(script.executeAsLibrary(fixture)).thenReturn(library);
        assertThat(fixture.lookup(name)).isEqualTo(library);
    }

    @Test
    void lookup_WhenAScriptIsExecuted_ThenTheValueIsCached() {
        var name = random.nextString();
        when(version.lookup(name)).thenReturn(script);
        when(script.executeAsLibrary(fixture)).thenReturn(library).thenThrow(AssertionError.class);
        assertThat(fixture.lookup(name)).isEqualTo(library);
        assertThat(fixture.lookup(name)).isEqualTo(library);
    }

    @Test
    void getGlobalRules(SeededRng random) {
        var rule = random.<Rule>get();
        when(version.getGlobalRules()).thenReturn(List.of(rule));
        assertThat(fixture.getGlobalRules()).containsExactly(rule);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void submitPlayerCommand(boolean complete, SeededRng random) throws CommandException {
        var ability1Name = random.<String>get();
        var ability2Name = random.not(ability1Name);
        var target1 = random.<JsonNode>get();
        var target2 = random.not(target1);
        var target1Value = new Object();
        var target2Value = new Object();
        var actor = fixture.getActivePlayer();
        when(actor.isReady()).thenReturn(true);
        when(version.lookup(ability1Name)).thenReturn(ability1);
        when(version.lookup(ability2Name)).thenReturn(ability2);
        when(ability1.unpack(actor, target1)).thenReturn(target1Value);
        when(ability2.unpack(actor, target2)).thenReturn(target2Value);
        when(ability1.execute(actor, target1Value)).thenReturn(true);
        when(ability2.execute(actor, target2Value)).thenReturn(complete);
        assertThat(fixture.submitPlayerCommand(List.of(
                new Command(ability1Name, target1),
                new Command(ability2Name, target2)
        ))).isEqualTo(complete);
        var inOrder = inOrder(ability1, ability2);
        inOrder.verify(ability1).unpack(actor, target1);
        inOrder.verify(ability2).unpack(actor, target2);
        inOrder.verify(ability1).validate(actor, target1Value);
        inOrder.verify(ability1).execute(actor, target1Value);
        inOrder.verify(ability2).validate(actor, target2Value);
        inOrder.verify(ability2).execute(actor, target2Value);
    }

    @Test
    void submitPlayerCommand_WhenACommandIsInterrupted_ThenSkipsTheRest(SeededRng random) throws CommandException {
        var ability1Name = random.<String>get();
        var ability2Name = random.not(ability1Name);
        var target1 = random.<JsonNode>get();
        var target2 = random.not(target1);
        var target1Value = new Object();
        var target2Value = new Object();
        var actor = fixture.getActivePlayer();
        when(actor.isReady()).thenReturn(true);
        when(version.lookup(ability1Name)).thenReturn(ability1);
        when(version.lookup(ability2Name)).thenReturn(ability2);
        when(ability1.unpack(actor, target1)).thenReturn(target1Value);
        when(ability2.unpack(actor, target2)).thenReturn(target2Value);
        when(ability1.execute(actor, target1Value)).thenReturn(false);
        assertThat(fixture.submitPlayerCommand(List.of(
                new Command(ability1Name, target1),
                new Command(ability2Name, target2)
        ))).isFalse();
        var inOrder = inOrder(ability1, ability2);
        inOrder.verify(ability1).unpack(actor, target1);
        inOrder.verify(ability2).unpack(actor, target2);
        inOrder.verify(ability1).validate(actor, target1Value);
        inOrder.verify(ability1).execute(actor, target1Value);
        verify(ability2, never()).validate(actor, target2Value);
        verify(ability2, never()).execute(actor, target2Value);
    }

    @Test
    void submitPlayerCommand_WhenThePlayerIsNotReady_ThenThrows(SeededRng random) throws CommandException {
        var ability1Name = random.<String>get();
        var ability2Name = random.not(ability1Name);
        var target1 = random.<JsonNode>get();
        var target2 = random.not(target1);
        var actor = fixture.getActivePlayer();
        when(actor.isReady()).thenReturn(false);
        when(version.lookup(ability1Name)).thenReturn(ability1);
        when(version.lookup(ability2Name)).thenReturn(ability2);
        assertThatThrownBy(() -> fixture.submitPlayerCommand(List.of(
                new Command(ability1Name, target1),
                new Command(ability2Name, target2)
        ))).isInstanceOf(CommandException.class).hasMessage("Invalid recipient for command.");
        verify(ability1, never()).validate(any(), any());
        verify(ability2, never()).validate(any(), any());
        verify(ability1, never()).execute(any(), any());
        verify(ability2, never()).execute(any(), any());
    }

    @RepeatedTest(3)
    void submitPlayerCommand_WhenAnAbilityIsUnknown_ThenThrows(RepetitionInfo repetitionInfo, SeededRng random) {
        var ability1Name = random.<String>get();
        var ability2Name = random.not(ability1Name);
        var target1 = random.<JsonNode>get();
        var target2 = random.not(target1);
        var actor = fixture.getActivePlayer();
        when(actor.isReady()).thenReturn(true);
        var problem = ability1Name;
        if (repetitionInfo.getCurrentRepetition() == 1) {
            when(version.lookup(ability1Name)).thenReturn(ability1);
            problem = ability2Name;
        } else if (repetitionInfo.getCurrentRepetition() == 2) {
            when(version.lookup(ability2Name)).thenReturn(ability2);
        }
        assertThatThrownBy(() -> fixture.submitPlayerCommand(List.of(
                new Command(ability1Name, target1),
                new Command(ability2Name, target2)
        ))).isInstanceOf(CommandException.class).hasMessage("Unknown ability '%s'.", problem);
    }

    @Test
    void submitPlayerCommand_WhenNoCommandsAreGiven_ThenThrows() {
        var actor = fixture.getActivePlayer();
        when(actor.isReady()).thenReturn(true);
        assertThatThrownBy(() -> fixture.submitPlayerCommand(List.of()))
                .isInstanceOf(CommandException.class)
                .hasMessage("At least one command must be given.");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void submitUnitCommand(boolean complete, SeededRng random) throws CommandException {
        var x = random.nextInt(0, 3);
        var y = random.nextInt(0, 2);
        var ability1Name = random.<String>get();
        var ability2Name = random.not(ability1Name);
        var target1 = random.<JsonNode>get();
        var target2 = random.not(target1);
        var target1Value = new Object();
        var target2Value = new Object();
        var tile = Objects.requireNonNull(fixture.getTile(x, y));
        when(tile.getUnit()).thenReturn(unit1);
        when(unit1.isReady()).thenReturn(true);
        when(version.lookup(ability1Name)).thenReturn(ability1);
        when(version.lookup(ability2Name)).thenReturn(ability2);
        when(ability1.unpack(unit1, target1)).thenReturn(target1Value);
        when(ability2.unpack(unit1, target2)).thenReturn(target2Value);
        when(ability1.execute(unit1, target1Value)).thenReturn(true);
        when(ability2.execute(unit1, target2Value)).thenReturn(complete);
        assertThat(fixture.submitUnitCommand(x, y, List.of(
                new Command(ability1Name, target1),
                new Command(ability2Name, target2)
        ))).isEqualTo(complete);
        var inOrder = inOrder(ability1, ability2);
        inOrder.verify(ability1).unpack(unit1, target1);
        inOrder.verify(ability2).unpack(unit1, target2);
        inOrder.verify(ability1).validate(unit1, target1Value);
        inOrder.verify(ability1).execute(unit1, target1Value);
        inOrder.verify(ability2).validate(unit1, target2Value);
        inOrder.verify(ability2).execute(unit1, target2Value);
    }

    @Test
    void submitUnitCommand_WhenACommandIsInterrupted_ThenSkipsTheRest(SeededRng random) throws CommandException {
        var x = random.nextInt(0, 3);
        var y = random.nextInt(0, 2);
        var ability1Name = random.<String>get();
        var ability2Name = random.not(ability1Name);
        var target1 = random.<JsonNode>get();
        var target2 = random.not(target1);
        var target1Value = new Object();
        var target2Value = new Object();
        var tile = Objects.requireNonNull(fixture.getTile(x, y));
        when(tile.getUnit()).thenReturn(unit1);
        when(unit1.isReady()).thenReturn(true);
        when(version.lookup(ability1Name)).thenReturn(ability1);
        when(version.lookup(ability2Name)).thenReturn(ability2);
        when(ability1.unpack(unit1, target1)).thenReturn(target1Value);
        when(ability2.unpack(unit1, target2)).thenReturn(target2Value);
        when(ability1.execute(unit1, target1Value)).thenReturn(false);
        assertThat(fixture.submitUnitCommand(x, y, List.of(
                new Command(ability1Name, target1),
                new Command(ability2Name, target2)
        ))).isFalse();
        var inOrder = inOrder(ability1, ability2);
        inOrder.verify(ability1).unpack(unit1, target1);
        inOrder.verify(ability2).unpack(unit1, target2);
        inOrder.verify(ability1).validate(unit1, target1Value);
        inOrder.verify(ability1).execute(unit1, target1Value);
        verify(ability2, never()).validate(unit1, target2Value);
        verify(ability2, never()).execute(unit1, target2Value);
    }

    @Test
    void submitUnitCommand_WhenTheUnitIsNotReady_ThenThrows(SeededRng random) throws CommandException {
        var x = random.nextInt(0, 3);
        var y = random.nextInt(0, 2);
        var ability1Name = random.<String>get();
        var ability2Name = random.not(ability1Name);
        var target1 = random.<JsonNode>get();
        var target2 = random.not(target1);
        var tile = Objects.requireNonNull(fixture.getTile(x, y));
        when(tile.getUnit()).thenReturn(unit1);
        when(unit1.isReady()).thenReturn(false);
        when(version.lookup(ability1Name)).thenReturn(ability1);
        when(version.lookup(ability2Name)).thenReturn(ability2);
        assertThatThrownBy(() -> fixture.submitUnitCommand(x, y, List.of(
                new Command(ability1Name, target1),
                new Command(ability2Name, target2)
        ))).isInstanceOf(CommandException.class).hasMessage("Invalid recipient for command.");
        verify(ability1, never()).validate(any(), any());
        verify(ability2, never()).validate(any(), any());
        verify(ability1, never()).execute(any(), any());
        verify(ability2, never()).execute(any(), any());
    }

    @Test
    void submitUnitCommand_WhenTheUnitIsDoesNotExist_ThenThrows(SeededRng random) throws CommandException {
        var x = random.nextInt(0, 3);
        var y = random.nextInt(0, 2);
        var ability1Name = random.<String>get();
        var ability2Name = random.not(ability1Name);
        var target1 = random.<JsonNode>get();
        var target2 = random.not(target1);
        var tile = Objects.requireNonNull(fixture.getTile(x, y));
        when(tile.getUnit()).thenReturn(null);
        when(version.lookup(ability1Name)).thenReturn(ability1);
        when(version.lookup(ability2Name)).thenReturn(ability2);
        assertThatThrownBy(() -> fixture.submitUnitCommand(x, y, List.of(
                new Command(ability1Name, target1),
                new Command(ability2Name, target2)
        ))).isInstanceOf(CommandException.class).hasMessage("Invalid recipient for command.");
        verify(ability1, never()).validate(any(), any());
        verify(ability2, never()).validate(any(), any());
        verify(ability1, never()).execute(any(), any());
        verify(ability2, never()).execute(any(), any());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            -1,0
            0,-1
            4,0
            0,3
            """)
    void submitUnitCommand_WhenTheTileIsOutOfBounds_ThenThrows(int x, int y, SeededRng random) throws CommandException {
        var ability1Name = random.<String>get();
        var ability2Name = random.not(ability1Name);
        var target1 = random.<JsonNode>get();
        var target2 = random.not(target1);
        when(version.lookup(ability1Name)).thenReturn(ability1);
        when(version.lookup(ability2Name)).thenReturn(ability2);
        assertThatThrownBy(() -> fixture.submitUnitCommand(x, y, List.of(
                new Command(ability1Name, target1),
                new Command(ability2Name, target2)
        ))).isInstanceOf(CommandException.class).hasMessage("Invalid coordinates for command.");
        verify(ability1, never()).validate(any(), any());
        verify(ability2, never()).validate(any(), any());
        verify(ability1, never()).execute(any(), any());
        verify(ability2, never()).execute(any(), any());
    }

    @RepeatedTest(3)
    void submitUnitCommand_WhenAnAbilityIsUnknown_ThenThrows(RepetitionInfo repetitionInfo, SeededRng random) {
        var x = random.nextInt(0, 3);
        var y = random.nextInt(0, 2);
        var ability1Name = random.<String>get();
        var ability2Name = random.not(ability1Name);
        var target1 = random.<JsonNode>get();
        var target2 = random.not(target1);
        var tile = Objects.requireNonNull(fixture.getTile(x, y));
        when(tile.getUnit()).thenReturn(unit1);
        when(unit1.isReady()).thenReturn(true);
        var problem = ability1Name;
        if (repetitionInfo.getCurrentRepetition() == 1) {
            when(version.lookup(ability1Name)).thenReturn(ability1);
            problem = ability2Name;
        } else if (repetitionInfo.getCurrentRepetition() == 2) {
            when(version.lookup(ability2Name)).thenReturn(ability2);
        }
        assertThatThrownBy(() -> fixture.submitUnitCommand(x, y, List.of(
                new Command(ability1Name, target1),
                new Command(ability2Name, target2)
        ))).isInstanceOf(CommandException.class).hasMessage("Unknown ability '%s'.", problem);
    }

    @Test
    void submitUnitCommand_WhenNoCommandsAreGiven_ThenThrows(SeededRng random) {
        var x = random.nextInt(0, 3);
        var y = random.nextInt(0, 2);
        var tile = Objects.requireNonNull(fixture.getTile(x, y));
        when(tile.getUnit()).thenReturn(unit1);
        assertThatThrownBy(() -> fixture.submitUnitCommand(x, y, List.of()))
                .isInstanceOf(CommandException.class)
                .hasMessage("At least one command must be given.");
    }

}
