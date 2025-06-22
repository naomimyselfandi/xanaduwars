package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.UnitData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.SpeedQuery;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.UnitDestructionEvent;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.UnitTagQuery;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.VisionRangeQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.UnitType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitImplTest {

    @Mock
    private Rule rule0, rule1, rule2, rule3;

    @Mock
    private Player alice, bob, charlie, dave;

    @Mock
    private NormalAction move, drop, wait, action0, action1;

    @Mock
    private Weapon weapon0, weapon1;

    @Mock
    private Tile tile;

    @Mock
    private Unit unit;

    @Mock
    private UnitType type, anotherType;

    private UnitData unitData;

    @Mock
    private Ruleset ruleset;

    @Mock
    private GameState gameState;

    private UnitImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        unitData = random.get();
        var id = random.<UnitId>get();
        fixture = new UnitImpl(unitData, gameState, id);
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(ruleset.getUnitType(unitData.getTypeId())).thenReturn(type);
    }

    @Test
    void getType() {
        assertThat(fixture.getType()).isEqualTo(type);
    }

    @Test
    void setType(SeededRng random) {
        when(anotherType.getId()).thenReturn(random.get());
        assertThat(fixture.setType(anotherType)).isEqualTo(fixture);
        assertThat(unitData.getTypeId()).isEqualTo(anotherType.getId());
        verify(gameState).invalidateCache();
    }

    @Test
    void getTags(SeededRng random) {
        var value = Set.<UnitTag>of(random.get(), random.get());
        when(gameState.evaluate(new UnitTagQuery(fixture))).thenReturn(value);
        assertThat(fixture.getTags()).isEqualTo(value);
    }

    @Test
    void getVision(SeededRng random) {
        var value = random.nextInt();
        when(gameState.evaluate(new VisionRangeQuery(fixture))).thenReturn(value);
        assertThat(fixture.getVision()).isEqualTo(value);
    }

    @Test
    void getSpeed(SeededRng random) {
        var value = random.nextInt();
        when(gameState.evaluate(new SpeedQuery(fixture))).thenReturn(value);
        assertThat(fixture.getSpeed()).isEqualTo(value);
    }

    @Test
    void getHp() {
        assertThat(fixture.getHp()).isEqualTo(unitData.getHp());
    }

    @Test
    void setHp(SeededRng random) {
        var hp = random.not(unitData.getHp(), Hp.ZERO);
        assertThat(fixture.setHp(hp)).isSameAs(fixture);
        assertThat(fixture.getHp()).isEqualTo(hp);
        assertThat(unitData.getHp()).isEqualTo(hp);
        verify(gameState).invalidateCache();
        verify(gameState, never()).evaluate(new UnitDestructionEvent(fixture));
    }

    @Test
    void setHp_WhenTheHpIsZero_ThenDestroysTheUnit() {
        assertThat(fixture.setHp(Hp.ZERO)).isSameAs(fixture);
        assertThat(fixture.getHp()).isEqualTo(Hp.ZERO);
        assertThat(unitData.getHp()).isEqualTo(Hp.ZERO);
        verify(gameState).evaluate(new UnitDestructionEvent(fixture));
    }

    @Test
    void getLocation_Tile(SeededRng random) {
        var nodeId = random.<TileId>get();
        unitData.setLocationId(nodeId);
        when(gameState.getTiles()).thenReturn(new TreeMap<>(Map.of(nodeId, tile)));
        assertThat(fixture.getLocation()).isEqualTo(tile);
        assertThat(fixture.getTile()).isEqualTo(tile);
    }

    @Test
    void getLocation_Unit(SeededRng random) {
        var nodeId = random.<UnitId>get();
        unitData.setLocationId(nodeId);
        when(gameState.getUnits()).thenReturn(new TreeMap<>(Map.of(nodeId, unit)));
        assertThat(fixture.getLocation()).isEqualTo(unit);
        assertThat(fixture.getTile()).isNull();
    }

    @Test
    void getCargo() {
        when(gameState.getUnit(fixture)).thenReturn(unit);
        assertThat(fixture.getCargo()).isEqualTo(unit);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isReady(boolean ready) {
        unitData.setReady(ready);
        assertThat(fixture.isReady()).isEqualTo(ready);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void setReady(boolean ready) {
        assertThat(fixture.setReady(ready)).isSameAs(fixture);
        assertThat(fixture.isReady()).isEqualTo(ready);
        assertThat(unitData.isReady()).isEqualTo(ready);
        verify(gameState).invalidateCache();
    }

    @Test
    void getOwner(SeededRng random) {
        var players = List.of(alice, bob, charlie, dave);
        when(gameState.getPlayers()).thenReturn(players);
        var player = random.pick(players);
        var index = players.indexOf(player);
        unitData.setPlayerId(new PlayerId(index));
        assertThat(fixture.getOwner()).isEqualTo(player);
    }

    @Test
    void setOwner(SeededRng random) {
        var playerId = random.<PlayerId>get();
        when(alice.getId()).thenReturn(playerId);
        assertThat(fixture.setOwner(alice)).isSameAs(fixture);
        assertThat(unitData.getPlayerId()).isEqualTo(playerId);
        verify(gameState).invalidateCache();
    }

    @Test
    void setOwner_Null(SeededRng random) {
        unitData.setPlayerId(random.get());
        fixture.setOwner(null);
        assertThat(unitData.getPlayerId()).isNull();
        verify(gameState).invalidateCache();
    }

    @Test
    void getOwner_WhenThePlayerIdIsNull_ThenNull() {
        unitData.setPlayerId(null);
        assertThat(fixture.getOwner()).isNull();
    }

    @Test
    void getActions() {
        when(ruleset.getMoveAction()).thenReturn(move);
        when(ruleset.getDropAction()).thenReturn(drop);
        when(ruleset.getWaitAction()).thenReturn(wait);
        when(type.getActions()).thenReturn(List.of(action0, action1));
        when(type.getWeapons()).thenReturn(List.of(weapon0, weapon1));
        var wrapper0 = new WeaponWrapper(fixture, weapon0);
        var wrapper1 = new WeaponWrapper(fixture, weapon1);
        assertThat(fixture.getActions()).containsExactly(move, drop, wrapper0, wrapper1, action0, action1, wait);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true
            false,true
            true,false
            false,false
            """)
    void getRules(boolean hasPlayer, boolean hasTile, SeededRng random) {
        when(alice.getRules()).thenReturn(List.of(rule0, rule1));
        when(tile.getRules()).thenReturn(List.of(rule2, rule3));
        var expected = new ArrayList<Rule>();
        expected.add(type);
        if (hasPlayer) {
            unitData.setPlayerId(new PlayerId(0));
            when(gameState.getPlayers()).thenReturn(List.of(alice));
            expected.add(rule0);
            expected.add(rule1);
        } else {
            unitData.setPlayerId(null);
        }
        if (hasTile) {
            var nodeId = random.<TileId>get();
            unitData.setLocationId(nodeId);
            when(gameState.getTiles()).thenReturn(new TreeMap<>(Map.of(nodeId, tile)));
            expected.add(rule2);
            expected.add(rule3);
        } else {
            unitData.setLocationId(random.<UnitId>get());
        }
        assertThat(fixture.getRules()).isEqualTo(expected);
    }

}
