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

import static org.assertj.core.api.Assertions.assertThat;
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
        tileData0 = random.nextTileData().setId(new TileId(0, 0));
        tileData1 = random.nextTileData().setId(new TileId(1, 0));
        var tileData2 = random.nextTileData().setId(new TileId(2, 0)).setStructureData(null);
        unitData0 = random.nextUnitData();
        unitData1 = random.nextUnitData();
        gameStateData = new GameStateData()
                .setPlayerData(List.of(aliceData, bobData, charlieData, daveData))
                .setTileData(List.of(tileData0, tileData1, tileData2))
                .setUnitData(new ArrayList<>(List.of(unitData0, unitData1)));
        when(tile0.getId()).thenReturn(new TileId(0, 0));
        when(gameState.getTiles()).thenReturn(List.of(tile0));
        var players = List.of(alice, bob, charlie, dave);
        for (var i = 0; i < players.size(); i++) {
            var player = players.get(i);
            when(player.getId()).thenReturn(new PlayerId(i));
        }
        when(gameState.getPlayers()).thenReturn(players);
        this.random = random;
    }

    @Test
    void redact_WhenAnUnseenTileHasAStructure_ThenRemovesIt() {
        when(alice.canSee(tile0)).thenReturn(false);
        tileData0.setStructureData(random.get()).setMemory(new Memory(Map.of()));
        fixture.redact(gameState, gameStateData, alice);
        assertThat(tileData0.getStructureData()).isNull();
        verifyNoInteractions(bob);
    }

    @Test
    void redact_WhenAnUnseenTileHasARememberedStructure_ThenAddsIt() {
        when(alice.canSee(tile0)).thenReturn(false);
        var type = random.<StructureTypeId>get();
        tileData0.setMemory(new Memory(Map.of(alice.getId(), type)));
        fixture.redact(gameState, gameStateData, alice);
        assertThat(tileData0.getStructureData()).isEqualTo(new StructureData()
                .setHp(Asset.FULL_HP)
                .setComplete(true)
                .setType(type));
        verifyNoInteractions(bob);
    }

    @Test
    void redact_WhenAnUnseenStructureIsRemembered_ThenRedactsIt() {
        when(alice.canSee(tile0)).thenReturn(false);
        var type = random.<StructureTypeId>get();
        tileData0.setStructureData(random.get()).setMemory(new Memory(Map.of(alice.getId(), type)));
        fixture.redact(gameState, gameStateData, alice);
        assertThat(tileData0.getStructureData()).isEqualTo(new StructureData()
                .setHp(Asset.FULL_HP)
                .setComplete(true)
                .setType(type));
        verifyNoInteractions(bob);
    }

    @Test
    void redact_WhenAStructureIsVisible_ThenDoesNothing() {
        when(alice.canSee(tile0)).thenReturn(true);
        var structureData = random.nextStructureData();
        var hashCode = structureData.hashCode();
        tileData0.setStructureData(structureData).setMemory(new Memory(Map.of()));
        fixture.redact(gameState, gameStateData, alice);
        assertThat(tileData0.getStructureData()).isEqualTo(structureData).returns(hashCode, StructureData::hashCode);
        verifyNoInteractions(bob);
    }

    @Test
    void redact_RemovesUnseenUnits() {
        when(alice.canSee(unit0)).thenReturn(false);
        var unit0Data = random.nextUnitData();
        when(unit0.getId()).thenReturn(unit0Data.getId());
        when(alice.canSee(unit1)).thenReturn(true);
        var unit1Data = random.nextUnitData();
        when(unit1.getId()).thenReturn(unit1Data.getId());
        gameStateData.setUnitData(new ArrayList<>(List.of(unit0Data, unit1Data)));
        when(gameState.getUnits()).then(_ -> Stream.of(unit0, unit1));
        fixture.redact(gameState, gameStateData, alice);
        assertThat(gameStateData.getUnitData()).containsOnly(unit1Data);
    }

    @Test
    void redact_RedactsResources(SeededRng random) {
        aliceData.setTeam(random.get());
        bobData.setTeam(random.get(aliceData.getTeam()));
        charlieData.setTeam(aliceData.getTeam());
        daveData.setTeam(random.get(aliceData.getTeam(), bobData.getTeam()));
        when(alice.getTeam()).thenReturn(aliceData.getTeam());
        var aliceSupplies = aliceData.getSupplies();
        var aliceAether = aliceData.getAether();
        var aliceFocus = aliceData.getFocus();
        var charlieSupplies = charlieData.getSupplies();
        var charlieAether = charlieData.getAether();
        var charlieFocus = charlieData.getFocus();
        fixture.redact(gameState, gameStateData, alice);
        assertThat(aliceData.getSupplies()).isEqualTo(aliceSupplies);
        assertThat(aliceData.getAether()).isEqualTo(aliceAether);
        assertThat(aliceData.getFocus()).isEqualTo(aliceFocus);
        assertThat(bobData.getSupplies()).isZero();
        assertThat(bobData.getAether()).isZero();
        assertThat(bobData.getFocus()).isZero();
        assertThat(charlieData.getSupplies()).isEqualTo(charlieSupplies);
        assertThat(charlieData.getAether()).isEqualTo(charlieAether);
        assertThat(charlieData.getFocus()).isEqualTo(charlieFocus);
        assertThat(daveData.getSupplies()).isZero();
        assertThat(daveData.getAether()).isZero();
        assertThat(daveData.getFocus()).isZero();
    }

    @Test
    void redact_WhenThereAreOnlyTwoTeams_ThenDoesNotRedactFocus(SeededRng random) {
        aliceData.setTeam(random.get());
        bobData.setTeam(random.get(aliceData.getTeam()));
        charlieData.setTeam(aliceData.getTeam());
        daveData.setTeam(bobData.getTeam());
        when(alice.getTeam()).thenReturn(aliceData.getTeam());
        var aliceSupplies = aliceData.getSupplies();
        var aliceAether = aliceData.getAether();
        var aliceFocus = aliceData.getFocus();
        var bobFocus = bobData.getFocus();
        var charlieSupplies = charlieData.getSupplies();
        var charlieAether = charlieData.getAether();
        var charlieFocus = charlieData.getFocus();
        var daveFocus = daveData.getFocus();
        fixture.redact(gameState, gameStateData, alice);
        assertThat(aliceData.getSupplies()).isEqualTo(aliceSupplies);
        assertThat(aliceData.getAether()).isEqualTo(aliceAether);
        assertThat(aliceData.getFocus()).isEqualTo(aliceFocus);
        assertThat(bobData.getSupplies()).isZero();
        assertThat(bobData.getAether()).isZero();
        assertThat(bobData.getFocus()).isEqualTo(bobFocus);
        assertThat(charlieData.getSupplies()).isEqualTo(charlieSupplies);
        assertThat(charlieData.getAether()).isEqualTo(charlieAether);
        assertThat(charlieData.getFocus()).isEqualTo(charlieFocus);
        assertThat(daveData.getSupplies()).isZero();
        assertThat(daveData.getAether()).isZero();
        assertThat(daveData.getFocus()).isEqualTo(daveFocus);
    }

    @Test
    void redact_RedactsSpellSlots(SeededRng random) {
        var aliceSlots = slots(random);
        var bobSlots = slots(random);
        var charlieSlots = slots(random);
        var daveSlots = slots(random);
        aliceData.setTeam(random.get()).setSpellSlots(aliceSlots);
        bobData.setTeam(random.get(aliceData.getTeam())).setSpellSlots(bobSlots);
        charlieData.setTeam(aliceData.getTeam()).setSpellSlots(charlieSlots);
        daveData.setTeam(random.get(aliceData.getTeam(), bobData.getTeam())).setSpellSlots(daveSlots);
        when(alice.getTeam()).thenReturn(aliceData.getTeam());
        fixture.redact(gameState, gameStateData, alice);
        assertThat(aliceData.getSpellSlots()).isEqualTo(aliceSlots);
        assertThat(charlieData.getSpellSlots()).isEqualTo(charlieSlots);
        assertThat(bobData.getSpellSlots().slots())
                .isEqualTo(bobSlots.slots().stream().filter(SpellSlotData::revealed).toList());
        assertThat(daveData.getSpellSlots().slots())
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
        tileData0.setStructureData(structureData).setMemory(new Memory(Map.of()));
        fixture.updateMemory(gameState, gameStateData);
        assertThat(tileData0.getMemory()).isEqualTo(new Memory(Map.of(alice.getId(), structureData.getType())));
    }

    @Test
    void updateMemory_WhenAStructureIsVisible_ThenOtherMemoriesAreLeftAlone() {
        when(alice.canSee(tile0)).thenReturn(true);
        when(bob.canSee(tile0)).thenReturn(false);
        var structureData = random.nextStructureData();
        when(bob.getId()).thenReturn(random.get());
        var otherTypeId = random.<StructureTypeId>get();
        tileData0.setStructureData(structureData).setMemory(new Memory(Map.of(bob.getId(), otherTypeId)));
        fixture.updateMemory(gameState, gameStateData);
        assertThat(tileData0.getMemory()).isEqualTo(new Memory(Map.of(
                alice.getId(), structureData.getType(),
                bob.getId(), otherTypeId
        )));
    }

    @Test
    void updateMemory_WhenAnEmptyTileIsVisible_ThenForgetsAnyRememberedStructure() {
        when(alice.canSee(tile0)).thenReturn(true);
        when(bob.canSee(tile0)).thenReturn(false);
        when(bob.getId()).thenReturn(random.get());
        var typeId = random.<StructureTypeId>get();
        tileData0.setMemory(new Memory(Map.of(alice.getId(), random.get(), bob.getId(), typeId)));
        fixture.updateMemory(gameState, gameStateData);
        assertThat(tileData0.getMemory()).isEqualTo(new Memory(Map.of(bob.getId(), typeId)));
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void cleanup(boolean structure, boolean unit, boolean recurse) {
        when(gameState.evaluate(any())).then(invocation -> {
            var type = invocation.getArgument(0).getClass();
            if (type == UnitDeathEvent.class || type == StructureDeathEvent.class) {
                assertThat(gameStateData.getUnitData()).hasSize(2);
                assertThat(gameStateData.getTileData())
                        .map(TileData::getStructureData)
                        .map(Objects::nonNull)
                        .containsExactly(true, true, false);
            }
            return None.NONE;
        });
        when(structure0.getTile()).thenReturn(tile0);
        when(tile0.getId()).thenReturn(tileData0.getId());
        tileData0.setStructureData(structureData0);
        when(structure1.getTile()).thenReturn(tile1);
        when(tile1.getId()).thenReturn(tileData1.getId());
        when(gameState.getStructures()).then(_ -> Stream.of(structure0, structure1));
        when(unit0.getId()).thenReturn(unitData0.getId());
        when(unit1.getId()).thenReturn(unitData1.getId());
        tileData1.setStructureData(structureData1);
        when(gameState.getUnits()).then(_ -> Stream.of(unit0, unit1));
        when(structure0.getHp()).thenReturn(random.nextInt(1, 101));
        when(structure1.getHp()).thenReturn(structure ? 0 : random.nextInt(1, 101));
        when(unit0.getHp()).thenReturn(random.nextInt(1, 101));
        when(unit1.getHp()).thenReturn(unit ? 0 : random.nextInt(1, 101));
        unitData0.setHp(unit0.getHp());
        unitData1.setHp(unit1.getHp());
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
        gameStateData.setTurn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        assertThat(gameStateData.getTurn()).isEqualTo(new Turn(turn + 1));
        verify(gameState).evaluate(new PlayerTurnEvent(player));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void pass_ClearsUnitActions(int activePlayer) {
        var player = List.of(alice, bob, charlie, dave).get((activePlayer + 1) % 4);
        when(player.getUnits()).then(_ -> Stream.of(unit0, unit1));
        var turn = random.nextInt(256) * 4 + activePlayer;
        gameStateData.setTurn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        verify(unit0).setHistory(List.of());
        verify(unit1).setHistory(List.of());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void pass_ClearsSpellcasting(int activePlayer, SeededRng random) {
        var index = (activePlayer + 1) % 4;
        var player = List.of(alice, bob, charlie, dave).get(index);
        var playerData = List.of(aliceData, bobData, charlieData, daveData).get(index);
        var spellSlotData = random.<SpellSlotData>get().withCasts(random.nextInt(1, 100));
        playerData.setSpellSlots(new SpellSlotList(List.of(spellSlotData)));
        doAnswer(_ -> {
            assertThat(playerData.getSpellSlots().slots()).containsExactly(spellSlotData);
            return None.NONE;
        }).when(gameState).evaluate(new PlayerTurnEvent(player));
        var turn = random.nextInt(256) * 4 + activePlayer;
        gameStateData.setTurn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        assertThat(playerData.getSpellSlots().slots()).containsExactly(spellSlotData.withCasts(0));
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
        when(player.getSupplies()).thenReturn(initialSupplies);
        when(player.getStructures()).then(_ -> Stream.of(structure0, structure1));
        when(structure0.getType()).thenReturn(structureType0);
        when(structure1.getType()).thenReturn(structureType1);
        when(structureType0.getSupplyIncome()).thenReturn(income1);
        when(structureType1.getSupplyIncome()).thenReturn(income2);
        gameStateData.setTurn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        verify(player).setSupplies(initialSupplies + income1 + income2);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void pass_AppliesAetherIncome(int activePlayer, SeededRng random) {
        var player = List.of(alice, bob, charlie, dave).get((activePlayer + 1) % 4);
        var turn = random.nextInt(256) * 4 + activePlayer;
        var initialAether = random.nextIntNotNegative();
        var income1 = random.nextIntNotNegative();
        var income2 = random.nextIntNotNegative();
        when(player.getAether()).thenReturn(initialAether);
        when(player.getStructures()).then(_ -> Stream.of(structure0, structure1));
        when(structure0.getType()).thenReturn(structureType0);
        when(structure1.getType()).thenReturn(structureType1);
        when(structureType0.getAetherIncome()).thenReturn(income1);
        when(structureType1.getAetherIncome()).thenReturn(income2);
        gameStateData.setTurn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        verify(player).setAether(initialAether + income1 + income2);
    }

    @Test
    void pass_SkipsDefeatedPlayers() {
        when(bob.isDefeated()).thenReturn(true);
        var turn = random.nextInt(256) * 4;
        gameStateData.setTurn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        assertThat(gameStateData.getTurn()).isEqualTo(new Turn(turn + 2));
        verify(gameState).evaluate(new PlayerTurnEvent(charlie));
    }

    @Test
    void pass_CanWrapAroundWhileSkippingDefeatedPlayers() {
        when(charlie.isDefeated()).thenReturn(true);
        when(dave.isDefeated()).thenReturn(true);
        var turn = random.nextInt(256) * 4 + 1;
        gameStateData.setTurn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        assertThat(gameStateData.getTurn()).isEqualTo(new Turn(turn + 3));
        verify(gameState).evaluate(new PlayerTurnEvent(alice));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void pass_WhenEveryPlayerIsDefeated_ThenDoesNothing(int activePlayer) {
        when(alice.isDefeated()).thenReturn(true);
        when(bob.isDefeated()).thenReturn(true);
        when(charlie.isDefeated()).thenReturn(true);
        when(dave.isDefeated()).thenReturn(true);
        var turn = random.nextInt(256) * 4 + activePlayer;
        gameStateData.setTurn(new Turn(turn));
        fixture.pass(gameState, gameStateData);
        assertThat(gameStateData.getTurn()).isEqualTo(new Turn(turn));
        verify(gameState, never()).evaluate(any());
    }

}
