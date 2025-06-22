package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.SpellId;
import io.github.naomimyselfandi.xanaduwars.core.common.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RedactorImplTest {

    private GameStateData gameStateData;

    @Mock
    private Player player;

    @Mock
    private Structure structure0, structure1;

    @Mock
    private StructureType structureType;

    @Mock
    private Tile tile0, tile1;

    @Mock
    private Unit unit0, unit1;

    @Mock
    private GameState gameState;

    private RedactorImpl fixture;

    @BeforeEach
    void setup() {
        gameStateData = new GameStateData();
        fixture = new RedactorImpl();
    }

    @Test
    void redact_Players(SeededRng random) {
        var team0 = random.get(Team.class);
        var team1 = random.not(team0);
        var player0 = random.<PlayerData>get().setTeam(team0);
        var player0Supplies = player0.getSupplies();
        var player0Aether = player0.getAether();
        var player0Focus = player0.getFocus();
        var player1 = random.<PlayerData>get().setTeam(team1);
        var player1Focus = player1.getFocus();
        var spell00 = random.<SpellId>get();
        var spell01 = random.not(spell00);
        var spell10 = random.not(spell00, spell01);
        var spell11 = random.not(spell00, spell01, spell10);
        player0.getChosenSpells().setSpellIds(List.of(spell00, spell01));
        player1.getChosenSpells().setSpellIds(List.of(spell10, spell11));
        player0.getChosenSpellRevelation().set(0, false);
        player0.getChosenSpellRevelation().set(1, true);
        player1.getChosenSpellRevelation().set(0, false);
        player1.getChosenSpellRevelation().set(1, true);
        gameStateData.setPlayers(new ArrayList<>(List.of(player0, player1)));
        when(player.getTeam()).thenReturn(team0);
        fixture.redact(gameState, gameStateData, player);
        assertThat(gameStateData.getPlayers()).containsExactly(player0, player1);
        assertThat(player0.getSupplies()).isEqualTo(player0Supplies);
        assertThat(player0.getAether()).isEqualTo(player0Aether);
        assertThat(player0.getFocus()).isEqualTo(player0Focus);
        assertThat(player1.getSupplies()).isZero();
        assertThat(player1.getAether()).isZero();
        assertThat(player1.getFocus()).isEqualTo(player1Focus);
        assertThat(player0.getChosenSpells().getSpellIds()).containsExactly(spell00, spell01);
        assertThat(player1.getChosenSpells().getSpellIds()).containsExactly(null, spell11);
    }

    @Test
    void redact_Structures(SeededRng random) {
        var id0 = random.<StructureId>get();
        when(structure0.getId()).thenReturn(id0);
        var gameStructure0 = random.<StructureData>get();
        var id1 = random.not(id0);
        when(structure1.getId()).thenReturn(id1);
        var gameStructure1 = random.not(gameStructure0);
        gameStateData.setStructures(new TreeMap<>(Map.of(id0, gameStructure0, id1, gameStructure1)));
        when(gameState.getStructures()).thenReturn(new TreeMap<>(Map.of(id0, structure0, id1, structure1)));
        when(player.canSee(structure0)).thenReturn(true);
        when(player.canSee(structure1)).thenReturn(false);
        fixture.redact(gameState, gameStateData, player);
        assertThat(gameStateData.getStructures()).hasSize(1).containsEntry(id0, gameStructure0);
    }

    @Test
    void redact_Tiles(SeededRng random) {
        var id0 = random.<TileId>get();
        when(tile0.getId()).thenReturn(id0);
        var id1 = random.<TileId>get();
        when(tile1.getId()).thenReturn(id1);
        var id2 = random.<StructureTypeId>get();
        when(structureType.getId()).thenReturn(id2);
        when(tile0.getMemory(player)).thenReturn(structureType);
        when(tile1.getMemory(player)).thenReturn(null);
        when(gameState.getTiles()).thenReturn(new TreeMap<>(Map.of(id0, tile0, id1, tile1)));
        fixture.redact(gameState, gameStateData, player);
        var structure = new StructureData().setTypeId(id2);
        assertThat(gameStateData.getStructures()).hasSize(1).containsEntry(id0.structureId(), structure);
    }

    @Test
    void redact_Units(SeededRng random) {
        var id0 = random.<UnitId>get();
        when(unit0.getId()).thenReturn(id0);
        var gameUnit0 = random.<UnitData>get();
        var id1 = random.not(id0);
        when(unit1.getId()).thenReturn(id1);
        var gameUnit1 = random.not(gameUnit0);
        gameStateData.setUnits(new TreeMap<>(Map.of(id0, gameUnit0, id1, gameUnit1)));
        when(gameState.getUnits()).thenReturn(new TreeMap<>(Map.of(id0, unit0, id1, unit1)));
        when(player.canSee(unit0)).thenReturn(true);
        when(player.canSee(unit1)).thenReturn(false);
        fixture.redact(gameState, gameStateData, player);
        assertThat(gameStateData.getUnits()).hasSize(1).containsEntry(id0, gameUnit0);
    }

}
