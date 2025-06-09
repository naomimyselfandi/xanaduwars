package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.*;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.CleanupEvent;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.PlayerTurnEvent;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.StructureDeathEvent;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.UnitDeathEvent;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStateHelperImplTest {

    @Mock
    private GameState gameState;

    @Mock
    private Player alice, bob, charlie, dave;

    @Mock
    private Tile tile0, tile1;

    @Mock
    private Unit unit0, unit1;

    @Mock
    private StructureType structureType0, structureType1;

    @Mock
    private Structure structure0, structure1;

    private PlayerData aliceData, bobData, charlieData, daveData;

    private StructureData structureData0, structureData1;

    private TileData tileData0;
    private TileData tileData1;

    private UnitData unitData0, unitData1;

    private GameStateData gameStateData;

    @Mock
    private GameStateHelper self;

    @InjectMocks
    private GameStateHelperImpl fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        aliceData = random.nextPlayerData();
        bobData = random.nextPlayerData();
        charlieData = random.nextPlayerData();
        daveData = random.nextPlayerData();
        structureData0 = random.nextStructureData();
        structureData1 = random.nextStructureData();
        tileData0 = random.nextTileData().id(new TileId(0, 0));
        tileData1 = random.nextTileData().id(new TileId(1, 0));
        var tileData2 = random.nextTileData().id(new TileId(2, 0)).structureData(null);
        unitData0 = random.nextUnitData();
        unitData1 = random.nextUnitData();
        gameStateData = new GameStateData()
                .playerData(List.of(aliceData, bobData, charlieData, daveData))
                .tileData(List.of(tileData0, tileData1, tileData2))
                .unitData(new ArrayList<>(List.of(unitData0, unitData1)));
        when(tile0.id()).thenReturn(new TileId(0, 0));
        when(gameState.tiles()).thenReturn(List.of(tile0));
        var players = List.of(alice, bob, charlie, dave);
        for (var i = 0; i < players.size(); i++) {
            var player = players.get(i);
            when(player.id()).thenReturn(new PlayerId(i));
        }
        when(gameState.players()).thenReturn(players);
        this.random = random;
    }

    @Test
    void redact_WhenAnUnseenTileHasAStructure_ThenRemovesIt() {
        when(alice.canSee(tile0)).thenReturn(false);
        tileData0.structureData(random.get()).memory(new Memory(Map.of()));
        fixture.redact(gameState, gameStateData, alice);
        assertThat(tileData0.structureData()).isNull();
        verifyNoInteractions(bob);
    }

    @Test
    void redact_WhenAnUnseenTileHasARememberedStructure_ThenAddsIt() {
        when(alice.canSee(tile0)).thenReturn(false);
        var type = random.<StructureTypeId>get();
        tileData0.memory(new Memory(Map.of(alice.id(), type)));
        fixture.redact(gameState, gameStateData, alice);
        assertThat(tileData0.structureData()).isEqualTo(new StructureData().hp(Asset.FULL_HP).complete(true).type(type));
        verifyNoInteractions(bob);
    }

    @Test
    void redact_WhenAnUnseenStructureIsRemembered_ThenRedactsIt() {
        when(alice.canSee(tile0)).thenReturn(false);
        var type = random.<StructureTypeId>get();
        tileData0.structureData(random.get()).memory(new Memory(Map.of(alice.id(), type)));
        fixture.redact(gameState, gameStateData, alice);
        assertThat(tileData0.structureData()).isEqualTo(new StructureData().hp(Asset.FULL_HP).complete(true).type(type));
        verifyNoInteractions(bob);
    }

    @Test
    void redact_WhenAStructureIsVisible_ThenDoesNothing() {
        when(alice.canSee(tile0)).thenReturn(true);
        var structureData = random.nextStructureData();
        var hashCode = structureData.hashCode();
        tileData0.structureData(structureData).memory(new Memory(Map.of()));
        fixture.redact(gameState, gameStateData, alice);
        assertThat(tileData0.structureData()).isEqualTo(structureData).returns(hashCode, StructureData::hashCode);
        verifyNoInteractions(bob);
    }

    @Test
    void redact_RemovesUnseenUnits() {
        when(alice.canSee(unit0)).thenReturn(false);
        var unit0Data = random.nextUnitData();
        when(unit0.id()).thenReturn(unit0Data.id());
        when(alice.canSee(unit1)).thenReturn(true);
        var unit1Data = random.nextUnitData();
        when(unit1.id()).thenReturn(unit1Data.id());
        gameStateData.unitData(new ArrayList<>(List.of(unit0Data, unit1Data)));
        when(gameState.units()).then(_ -> Stream.of(unit0, unit1));
        fixture.redact(gameState, gameStateData, alice);
        assertThat(gameStateData.unitData()).containsOnly(unit1Data);
    }

    @Test
    void redact_RedactsResources(SeededRng random) {
        aliceData.team(random.get());
        bobData.team(random.get(aliceData.team()));
        charlieData.team(aliceData.team());
        daveData.team(random.get(aliceData.team(), bobData.team()));
        when(alice.team()).thenReturn(aliceData.team());
        var aliceSupplies = aliceData.supplies();
        var aliceAether = aliceData.aether();
        var aliceFocus = aliceData.focus();
        var charlieSupplies = charlieData.supplies();
        var charlieAether = charlieData.aether();
        var charlieFocus = charlieData.focus();
        fixture.redact(gameState, gameStateData, alice);
        assertThat(aliceData.supplies()).isEqualTo(aliceSupplies);
        assertThat(aliceData.aether()).isEqualTo(aliceAether);
        assertThat(aliceData.focus()).isEqualTo(aliceFocus);
        assertThat(bobData.supplies()).isZero();
        assertThat(bobData.aether()).isZero();
        assertThat(bobData.focus()).isZero();
        assertThat(charlieData.supplies()).isEqualTo(charlieSupplies);
        assertThat(charlieData.aether()).isEqualTo(charlieAether);
        assertThat(charlieData.focus()).isEqualTo(charlieFocus);
        assertThat(daveData.supplies()).isZero();
        assertThat(daveData.aether()).isZero();
        assertThat(daveData.focus()).isZero();
    }

    @Test
    void redact_WhenThereAreOnlyTwoTeams_ThenDoesNotRedactFocus(SeededRng random) {
        aliceData.team(random.get());
        bobData.team(random.get(aliceData.team()));
        charlieData.team(aliceData.team());
        daveData.team(bobData.team());
        when(alice.team()).thenReturn(aliceData.team());
        var aliceSupplies = aliceData.supplies();
        var aliceAether = aliceData.aether();
        var aliceFocus = aliceData.focus();
        var bobFocus = bobData.focus();
        var charlieSupplies = charlieData.supplies();
        var charlieAether = charlieData.aether();
        var charlieFocus = charlieData.focus();
        var daveFocus = daveData.focus();
        fixture.redact(gameState, gameStateData, alice);
        assertThat(aliceData.supplies()).isEqualTo(aliceSupplies);
        assertThat(aliceData.aether()).isEqualTo(aliceAether);
        assertThat(aliceData.focus()).isEqualTo(aliceFocus);
        assertThat(bobData.supplies()).isZero();
        assertThat(bobData.aether()).isZero();
        assertThat(bobData.focus()).isEqualTo(bobFocus);
        assertThat(charlieData.supplies()).isEqualTo(charlieSupplies);
        assertThat(charlieData.aether()).isEqualTo(charlieAether);
        assertThat(charlieData.focus()).isEqualTo(charlieFocus);
        assertThat(daveData.supplies()).isZero();
        assertThat(daveData.aether()).isZero();
        assertThat(daveData.focus()).isEqualTo(daveFocus);
    }

    @Test
    void redact_RedactsSpellSlots(SeededRng random) {
        var aliceSlots = slots(random);
        var bobSlots = slots(random);
        var charlieSlots = slots(random);
        var daveSlots = slots(random);
        aliceData.team(random.get()).spellSlots(aliceSlots);
        bobData.team(random.get(aliceData.team())).spellSlots(bobSlots);
        charlieData.team(aliceData.team()).spellSlots(charlieSlots);
        daveData.team(random.get(aliceData.team(), bobData.team())).spellSlots(daveSlots);
        when(alice.team()).thenReturn(aliceData.team());
        fixture.redact(gameState, gameStateData, alice);
        assertThat(aliceData.spellSlots()).isEqualTo(aliceSlots);
        assertThat(charlieData.spellSlots()).isEqualTo(charlieSlots);
        assertThat(bobData.spellSlots().slots())
                .isEqualTo(bobSlots.slots().stream().filter(SpellSlotData::revealed).toList());
        assertThat(daveData.spellSlots().slots())
                .isEqualTo(daveSlots.slots().stream().filter(SpellSlotData::revealed).toList());
    }

    private static SpellSlotList slots(SeededRng random) {
        SpellSlotList result;
        do {
            result = random.get();
        } while (result.slots().stream().allMatch(SpellSlotData::revealed));
        return result;
    }

    @Test
    void updateMemory_WhenAStructureIsVisible_ThenRemembersIt() {
        when(alice.canSee(tile0)).thenReturn(true);
        when(bob.canSee(tile0)).thenReturn(false);
        var structureData = random.nextStructureData();
        tileData0.structureData(structureData).memory(new Memory(Map.of()));
        fixture.updateMemory(gameState, gameStateData);
        assertThat(tileData0.memory()).isEqualTo(new Memory(Map.of(alice.id(), structureData.type())));
    }

    @Test
    void updateMemory_WhenAStructureIsVisible_ThenOtherMemoriesAreLeftAlone() {
        when(alice.canSee(tile0)).thenReturn(true);
        when(bob.canSee(tile0)).thenReturn(false);
        var structureData = random.nextStructureData();
        when(bob.id()).thenReturn(random.get());
        var otherTypeId = random.<StructureTypeId>get();
        tileData0.structureData(structureData).memory(new Memory(Map.of(bob.id(), otherTypeId)));
        fixture.updateMemory(gameState, gameStateData);
        assertThat(tileData0.memory()).isEqualTo(new Memory(Map.of(
                alice.id(), structureData.type(),
                bob.id(), otherTypeId
        )));
    }

    @Test
    void updateMemory_WhenAnEmptyTileIsVisible_ThenForgetsAnyRememberedStructure() {
        when(alice.canSee(tile0)).thenReturn(true);
        when(bob.canSee(tile0)).thenReturn(false);
        when(bob.id()).thenReturn(random.get());
        var typeId = random.<StructureTypeId>get();
        tileData0.memory(new Memory(Map.of(alice.id(), random.get(), bob.id(), typeId)));
        fixture.updateMemory(gameState, gameStateData);
        assertThat(tileData0.memory()).isEqualTo(new Memory(Map.of(bob.id(), typeId)));
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void cleanup(boolean structure, boolean unit, boolean recurse) {
        when(gameState.evaluate(any())).then(invocation -> {
            var type = invocation.getArgument(0).getClass();
            if (type == UnitDeathEvent.class || type == StructureDeathEvent.class) {
                assertThat(gameStateData.unitData()).hasSize(2);
                assertThat(gameStateData.tileData())
                        .map(TileData::structureData)
                        .map(Objects::nonNull)
                        .containsExactly(true, true, false);
            }
            return None.NONE;
        });
        when(structure0.tile()).thenReturn(tile0);
        when(tile0.id()).thenReturn(tileData0.id());
        tileData0.structureData(structureData0);
        when(structure1.tile()).thenReturn(tile1);
        when(tile1.id()).thenReturn(tileData1.id());
        when(gameState.structures()).then(_ -> Stream.of(structure0, structure1));
        when(unit0.id()).thenReturn(unitData0.id());
        when(unit1.id()).thenReturn(unitData1.id());
        tileData1.structureData(structureData1);
        when(gameState.units()).then(_ -> Stream.of(unit0, unit1));
        when(structure0.hp()).thenReturn(random.nextInt(1, 101));
        when(structure1.hp()).thenReturn(structure ? 0 : random.nextInt(1, 101));
        when(unit0.hp()).thenReturn(random.nextInt(1, 101));
        when(unit1.hp()).thenReturn(unit ? 0 : random.nextInt(1, 101));
        unitData0.hp(unit0.hp());
        unitData1.hp(unit1.hp());
        fixture.cleanup(gameState, gameStateData);
        var inOrder = inOrder(gameState, self);
        if (structure) {
            inOrder.verify(gameState).evaluate(new StructureDeathEvent(structure1));
        } else {
            verify(gameState, never()).evaluate(new StructureDeathEvent(structure1));
        }
        if (unit) {
            inOrder.verify(gameState).evaluate(new UnitDeathEvent(unit1));
        } else {
            verify(gameState, never()).evaluate(new UnitDeathEvent(unit1));
        }
        if (recurse) {
            inOrder.verify(gameState).evaluate(new CleanupEvent(gameState));
            inOrder.verify(self).cleanup(gameState, gameStateData);
        } else {
            verify(gameState, never()).evaluate(new CleanupEvent(gameState));
            verify(self, never()).cleanup(gameState, gameStateData);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void pass(int activePlayer) {
        var player = List.of(alice, bob, charlie, dave).get((activePlayer + 1) % 4);
        var turn = random.nextInt(256) * 4 + activePlayer;
        gameStateData.turn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        assertThat(gameStateData.turn()).isEqualTo(new Turn(turn + 1));
        verify(gameState).evaluate(new PlayerTurnEvent(player));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void pass_ClearsUnitActions(int activePlayer) {
        var player = List.of(alice, bob, charlie, dave).get((activePlayer + 1) % 4);
        when(player.units()).then(_ -> Stream.of(unit0, unit1));
        var turn = random.nextInt(256) * 4 + activePlayer;
        gameStateData.turn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        verify(unit0).actionsThisTurn(List.of());
        verify(unit1).actionsThisTurn(List.of());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void pass_ClearsSpellcasting(int activePlayer, SeededRng random) {
        var index = (activePlayer + 1) % 4;
        var player = List.of(alice, bob, charlie, dave).get(index);
        var playerData = List.of(aliceData, bobData, charlieData, daveData).get(index);
        var spellSlotData = random.<SpellSlotData>get().withCasts(random.nextInt(1, 100));
        playerData.spellSlots(new SpellSlotList(List.of(spellSlotData)));
        doAnswer(_ -> {
            assertThat(playerData.spellSlots().slots()).containsExactly(spellSlotData);
            return None.NONE;
        }).when(gameState).evaluate(new PlayerTurnEvent(player));
        var turn = random.nextInt(256) * 4 + activePlayer;
        gameStateData.turn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        assertThat(playerData.spellSlots().slots()).containsExactly(spellSlotData.withCasts(0));
        verify(gameState).evaluate(new PlayerTurnEvent(player));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void pass_AppliesSupplyIncome(int activePlayer, SeededRng random) {
        var player = List.of(alice, bob, charlie, dave).get((activePlayer + 1) % 4);
        var turn = random.nextInt(256) * 4 + activePlayer;
        var initialSupplies = random.nextIntNotNegative();
        var income1 = random.nextIntNotNegative();
        var income2 = random.nextIntNotNegative();
        when(player.supplies()).thenReturn(initialSupplies);
        when(player.structures()).then(_ -> Stream.of(structure0, structure1));
        when(structure0.type()).thenReturn(structureType0);
        when(structure1.type()).thenReturn(structureType1);
        when(structureType0.supplyIncome()).thenReturn(income1);
        when(structureType1.supplyIncome()).thenReturn(income2);
        gameStateData.turn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        verify(player).supplies(initialSupplies + income1 + income2);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void pass_AppliesAetherIncome(int activePlayer, SeededRng random) {
        var player = List.of(alice, bob, charlie, dave).get((activePlayer + 1) % 4);
        var turn = random.nextInt(256) * 4 + activePlayer;
        var initialAether = random.nextIntNotNegative();
        var income1 = random.nextIntNotNegative();
        var income2 = random.nextIntNotNegative();
        when(player.aether()).thenReturn(initialAether);
        when(player.structures()).then(_ -> Stream.of(structure0, structure1));
        when(structure0.type()).thenReturn(structureType0);
        when(structure1.type()).thenReturn(structureType1);
        when(structureType0.aetherIncome()).thenReturn(income1);
        when(structureType1.aetherIncome()).thenReturn(income2);
        gameStateData.turn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        verify(player).aether(initialAether + income1 + income2);
    }

    @Test
    void pass_SkipsDefeatedPlayers() {
        when(bob.defeated()).thenReturn(true);
        var turn = random.nextInt(256) * 4;
        gameStateData.turn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        assertThat(gameStateData.turn()).isEqualTo(new Turn(turn + 2));
        verify(gameState).evaluate(new PlayerTurnEvent(charlie));
    }

    @Test
    void pass_CanWrapAroundWhileSkippingDefeatedPlayers() {
        when(charlie.defeated()).thenReturn(true);
        when(dave.defeated()).thenReturn(true);
        var turn = random.nextInt(256) * 4 + 1;
        gameStateData.turn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        assertThat(gameStateData.turn()).isEqualTo(new Turn(turn + 3));
        verify(gameState).evaluate(new PlayerTurnEvent(alice));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void pass_WhenEveryPlayerIsDefeated_ThenDoesNothing(int activePlayer) {
        when(alice.defeated()).thenReturn(true);
        when(bob.defeated()).thenReturn(true);
        when(charlie.defeated()).thenReturn(true);
        when(dave.defeated()).thenReturn(true);
        var turn = random.nextInt(256) * 4 + activePlayer;
        gameStateData.turn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        assertThat(gameStateData.turn()).isEqualTo(new Turn(turn));
        verify(gameState, never()).evaluate(any());
    }

}
