package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.SpellSlotData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.SpellSlotList;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.DefeatEvent;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.*;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class PlayerImplTest {

    @Mock
    private Commander commander;

    @Mock
    private Action action;

    @Mock
    private Player anotherPlayer;

    @Mock
    private Tile tile0, tile1, tile2;

    @Mock
    private Unit unit0, unit1;

    @Mock
    private Structure structure0, structure1;

    private PlayerData playerData;

    @Mock
    private Ruleset ruleset;

    @Mock
    private GameState gameState;

    private PlayerImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        playerData = random.nextPlayerData();
        when(ruleset.getCommander(playerData.getCommander())).thenReturn(commander);
        fixture = new PlayerImpl(playerData, gameState, ruleset);
    }

    @Test
    void getId() {
        assertThat(fixture.getId()).isEqualTo(playerData.getId());
    }

    @Test
    void getTeam() {
        assertThat(fixture.getTeam()).isEqualTo(playerData.getTeam());
    }

    @Test
    void getCommander() {
        assertThat(fixture.getCommander()).isEqualTo(commander);
    }

    @Test
    void getTags() {
        assertThat(fixture.getTags()).isEmpty();
    }

    @Test
    void getSpellSlots() {
        var list = playerData.getSpellSlots().slots();
        for (var i = 0; i < list.size(); i++) {
            assertThat(fixture.getSpellSlots()).element(i).isEqualTo(new SpellSlotImpl(list.get(i), ruleset));
        }
    }

    @Test
    void getUnits() {
        when(gameState.getUnits()).then(_ -> Stream.of(unit0, unit1));
        when(unit0.getOwner()).thenReturn(fixture);
        when(unit1.getOwner()).thenReturn(anotherPlayer);
        assertThat(fixture.getUnits()).containsExactly(unit0);
    }

    @Test
    void getStructures() {
        when(gameState.getTiles()).thenReturn(List.of(tile0, tile1, tile2));
        when(tile0.getStructure()).thenReturn(structure0);
        when(tile2.getStructure()).thenReturn(structure1);
        when(structure0.getOwner()).thenReturn(fixture);
        when(structure1.getOwner()).thenReturn(anotherPlayer);
        assertThat(fixture.getStructures()).containsExactly(structure0);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void canSee_Tile(boolean seenByUnit, boolean seenByStructure, boolean expected) {
        when(gameState.getUnits()).then(_ -> Stream.of(unit0));
        when(unit0.getOwner()).thenReturn(fixture);
        when(gameState.getTiles()).thenReturn(List.of(tile0));
        when(tile0.getStructure()).thenReturn(structure0);
        when(structure0.getOwner()).thenReturn(fixture);
        when(unit0.canSee(tile1)).thenReturn(seenByUnit);
        when(structure0.canSee(tile1)).thenReturn(seenByStructure);
        assertThat(fixture.canSee(tile1)).isEqualTo(expected);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void canSee_Unit(boolean friend, boolean seenByUnit, boolean seenByStructure, boolean expected) {
        when(gameState.getUnits()).then(_ -> Stream.of(unit0));
        when(unit0.getOwner()).thenReturn(fixture);
        when(gameState.getTiles()).thenReturn(List.of(tile0));
        when(tile0.getStructure()).thenReturn(structure0);
        when(structure0.getOwner()).thenReturn(fixture);
        when(unit1.getOwner()).thenReturn(anotherPlayer);
        when(anotherPlayer.getTeam()).thenReturn(friend ? fixture.getTeam() : new Team(fixture.getTeam().index() + 1));
        when(unit1.getTile()).thenReturn(tile1);
        when(unit0.canSee(tile1)).thenReturn(seenByUnit);
        when(structure0.canSee(tile1)).thenReturn(seenByStructure);
        assertThat(fixture.canSee(unit1)).isEqualTo(expected);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void canSee_NeutralUnit(boolean seenByUnit, boolean seenByStructure, boolean expected) {
        when(gameState.getUnits()).then(_ -> Stream.of(unit0));
        when(unit0.getOwner()).thenReturn(fixture);
        when(gameState.getTiles()).thenReturn(List.of(tile0));
        when(tile0.getStructure()).thenReturn(structure0);
        when(structure0.getOwner()).thenReturn(fixture);
        when(unit1.getOwner()).thenReturn(null);
        when(unit1.getTile()).thenReturn(tile1);
        when(unit0.canSee(tile1)).thenReturn(seenByUnit);
        when(structure0.canSee(tile1)).thenReturn(seenByStructure);
        assertThat(fixture.canSee(unit1)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void canSee_Unit_InTransport(boolean expected) {
        when(unit0.getOwner()).thenReturn(anotherPlayer);
        when(anotherPlayer.getTeam()).thenReturn(expected ? fixture.getTeam() : new Team(fixture.getTeam().index() + 1));
        assertThat(fixture.canSee(unit0)).isEqualTo(expected);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void canSee_Structure(boolean friend, boolean seenByUnit, boolean seenByStructure, boolean expected) {
        when(gameState.getUnits()).then(_ -> Stream.of(unit0));
        when(unit0.getOwner()).thenReturn(fixture);
        when(gameState.getTiles()).thenReturn(List.of(tile0));
        when(tile0.getStructure()).thenReturn(structure0);
        when(structure0.getOwner()).thenReturn(fixture);
        when(structure1.getOwner()).thenReturn(anotherPlayer);
        when(anotherPlayer.getTeam()).thenReturn(friend ? fixture.getTeam() : new Team(fixture.getTeam().index() + 1));
        when(structure1.getTile()).thenReturn(tile1);
        when(unit0.canSee(tile1)).thenReturn(seenByUnit);
        when(structure0.canSee(tile1)).thenReturn(seenByStructure);
        assertThat(fixture.canSee(structure1)).isEqualTo(expected);
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void canSee_NeutralStructure(boolean seenByUnit, boolean seenByStructure, boolean expected) {
        when(gameState.getUnits()).then(_ -> Stream.of(unit0));
        when(unit0.getOwner()).thenReturn(fixture);
        when(gameState.getTiles()).thenReturn(List.of(tile0));
        when(tile0.getStructure()).thenReturn(structure0);
        when(structure0.getOwner()).thenReturn(fixture);
        when(structure1.getOwner()).thenReturn(null);
        when(structure1.getTile()).thenReturn(tile1);
        when(unit0.canSee(tile1)).thenReturn(seenByUnit);
        when(structure0.canSee(tile1)).thenReturn(seenByStructure);
        assertThat(fixture.canSee(structure1)).isEqualTo(expected);
    }

    @Test
    void isDefeated() {
        playerData.setDefeated(false);
        assertThat(fixture.isDefeated()).isFalse();
        assertThat(fixture.defeat()).isSameAs(fixture);
        assertThat(fixture.isDefeated()).isTrue();
        assertThat(playerData.isDefeated()).isTrue();
        verify(gameState).evaluate(new DefeatEvent(fixture));
    }

    @Test
    void supplies(SeededRng random) {
        assertThat(fixture.getSupplies()).isEqualTo(playerData.getSupplies());
        var supplies = random.nextIntNotNegative();
        assertThat(fixture.setSupplies(supplies)).isSameAs(fixture);
        assertThat(fixture.getSupplies()).isEqualTo(supplies);
        assertThat(playerData.getSupplies()).isEqualTo(supplies);
    }

    @Test
    void aether(SeededRng random) {
        assertThat(fixture.getAether()).isEqualTo(playerData.getAether());
        var aether = random.nextIntNotNegative();
        assertThat(fixture.setAether(aether)).isSameAs(fixture);
        assertThat(fixture.getAether()).isEqualTo(aether);
        assertThat(playerData.getAether()).isEqualTo(aether);
    }

    @Test
    void focus(SeededRng random) {
        assertThat(fixture.getFocus()).isEqualTo(playerData.getFocus());
        var focus = random.nextIntNotNegative();
        assertThat(fixture.setFocus(focus)).isSameAs(fixture);
        assertThat(fixture.getFocus()).isEqualTo(focus);
        assertThat(playerData.getFocus()).isEqualTo(focus);
    }

    @Test
    void getAction(SeededRng random) {
        var spells = IntStream.range(0, 8).<Spell>mapToObj(_ -> mock()).toList();
        var spellId0 = new SpellId(random.nextInt(0, 8));
        var spellId1 = new SpellId(random.nextInt(0, 8));
        var spellId2 = new SpellId(random.nextInt(0, 8));
        when(ruleset.getSpells()).thenReturn(spells);
        playerData.setSpellSlots(new SpellSlotList(List.of(
                new SpellSlotData(spellId0, random.nextBoolean(), random.nextIntNotNegative()),
                new SpellSlotData(spellId1, random.nextBoolean(), random.nextIntNotNegative()),
                new SpellSlotData(spellId2, random.nextBoolean(), random.nextIntNotNegative())
        )));
        when(ruleset.getCommonPlayerActions()).thenReturn(List.of(action));
        assertThat(fixture.getAction()).containsExactly(
                spells.get(spellId0.index()),
                spells.get(spellId1.index()),
                spells.get(spellId2.index()),
                action
        );
    }

    @RepeatedTest(2)
    void rules(SeededRng random) {
        var spells = IntStream.range(0, 8).<Spell>mapToObj(_ -> mock()).toList();
        var spellId0 = new SpellId(random.nextInt(0, 8));
        var spellId1 = new SpellId(random.nextInt(0, 8));
        when(ruleset.getSpells()).thenReturn(spells);
        playerData.setSpellSlots(new SpellSlotList(List.of(
                new SpellSlotData(spellId0, random.nextBoolean(), random.nextInt(1, Integer.MAX_VALUE)),
                new SpellSlotData(spellId1, random.nextBoolean(), 0)
        )));
        assertThat(fixture.rules()).containsExactly(commander, spells.get(spellId0.index()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isSelf(boolean isSelf) {
        when(unit0.getOwner()).thenReturn(isSelf ? fixture : anotherPlayer);
        assertThat(fixture.isSelf(unit0)).isEqualTo(isSelf);
    }

    @Test
    void isSelf_WhenTheArgumentIsNull_ThenFalse() {
        assertThat(fixture.isSelf(null)).isFalse();
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isFriend(@Nullable Boolean friend, SeededRng random) {
        if (friend != null) {
            when(unit0.getOwner()).thenReturn(anotherPlayer);
            when(anotherPlayer.getTeam()).thenReturn(friend ? playerData.getTeam() : random.get(playerData.getTeam()));
        }
        assertThat(fixture.isFriend(unit0)).isEqualTo(Boolean.TRUE.equals(friend));
    }

    @Test
    void isFriend_WhenTheArgumentIsNull_ThenFalse() {
        assertThat(fixture.isFriend(null)).isFalse();
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isFoe(@Nullable Boolean foe, SeededRng random) {
        if (foe != null) {
            when(unit0.getOwner()).thenReturn(anotherPlayer);
            when(anotherPlayer.getTeam()).thenReturn(foe ? random.get(playerData.getTeam()) : playerData.getTeam());
        }
        assertThat(fixture.isFoe(unit0)).isEqualTo(Boolean.TRUE.equals(foe));
    }

    @Test
    void isFoe_WhenTheArgumentIsNull_ThenFalse() {
        assertThat(fixture.isFoe(null)).isFalse();
    }

    @Test
    void getOwner() {
        assertThat(fixture.getOwner()).isSameAs(fixture);
    }

    @Test
    void testToString() {
        var index = playerData.getId().playerId();
        assertThat(fixture).hasToString("Player[id=%d, commander=%s]", index, commander);
    }

}
