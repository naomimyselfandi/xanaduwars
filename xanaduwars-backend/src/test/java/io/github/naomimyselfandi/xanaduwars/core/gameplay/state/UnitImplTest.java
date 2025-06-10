package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.History;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.UnitData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.SpeedQuery;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.VisionCheckQuery;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.VisionRangeQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.*;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitImplTest {

    @Mock
    private Rule rule;

    @Mock
    private Weapon weapon;

    @Mock
    private Action ability, commonAction;

    @Mock
    private Player player;

    @Mock
    private Structure structure;

    @Mock
    private Tile tile, anotherTile;

    @Mock
    private Unit unit;

    @Mock
    private UnitType unitType;

    private UnitData unitData;

    @Mock
    private Ruleset ruleset;

    @Mock
    private GameState gameState;

    private UnitImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        unitData = random.get();
        when(ruleset.getUnitType(unitData.getType())).thenReturn(unitType);
        fixture = new UnitImpl(unitData, gameState, ruleset);
    }

    @Test
    void getId() {
        assertThat(fixture.getId()).isEqualTo(unitData.getId());
    }

    @Test
    void getType() {
        assertThat(fixture.getType()).isEqualTo(unitType);
    }

    @Test
    void getTags(SeededRng random) {
        when(unitType.getTags()).thenReturn(Set.of(random.get(), random.get()));
        assertThat(fixture.getTags()).isEqualTo(unitType.getTags());
    }

    @Test
    void location_Tile(SeededRng random) {
        var tileId = random.<TileId>get();
        when(tile.getId()).thenReturn(tileId);
        when(gameState.getTile(tileId)).thenReturn(tile);
        assertThat(fixture.setLocation(tile)).isSameAs(fixture);
        assertThat(fixture.getLocation()).isEqualTo(tile);
        assertThat(fixture.getTile()).isEqualTo(tile);
        assertThat(unitData.getLocation()).isEqualTo(tileId);
        var distance = random.nextInt();
        when(tile.getDistance(unit)).thenReturn((double) distance);
        assertThat(fixture.getDistance(unit)).isEqualTo(distance);
    }

    @Test
    void location_Unit(SeededRng random) {
        var unitId = random.<UnitId>get();
        when(unit.getId()).thenReturn(unitId);
        when(gameState.getUnit(unitId)).thenReturn(unit);
        assertThat(fixture.setLocation(unit)).isSameAs(fixture);
        assertThat(fixture.getLocation()).isEqualTo(unit);
        assertThat(fixture.getTile()).isNull();
        assertThat(unitData.getLocation()).isEqualTo(unitId);
        assertThat(fixture.getDistance(tile)).isNaN();
    }

    @Test
    void getTerrain_WhenTheUnitIsInAnotherUnit_ThenNull(SeededRng random) {
        var unitId = random.<UnitId>get();
        when(unit.getId()).thenReturn(unitId);
        when(gameState.getUnit(unitId)).thenReturn(unit);
        assertThat(fixture.setLocation(unit)).isSameAs(fixture);
        assertThat(fixture.getTerrain()).isNull();
    }

    @Test
    void getTerrain_WhenTheUnitIsOnATileWithNoStructure_ThenReturnsTheTile(SeededRng random) {
        var tileId = random.<TileId>get();
        when(tile.getId()).thenReturn(tileId);
        when(gameState.getTile(tileId)).thenReturn(tile);
        when(tile.getStructure()).thenReturn(null);
        assertThat(fixture.setLocation(tile)).isSameAs(fixture);
        assertThat(fixture.getTerrain()).isEqualTo(tile);
    }

    @Test
    void getTerrain_WhenTheUnitIsOnATileWithAStructure_ThenReturnsTheStructure(SeededRng random) {
        var tileId = random.<TileId>get();
        when(tile.getId()).thenReturn(tileId);
        when(gameState.getTile(tileId)).thenReturn(tile);
        when(tile.getStructure()).thenReturn(structure);
        assertThat(fixture.setLocation(tile)).isSameAs(fixture);
        assertThat(fixture.getTerrain()).isEqualTo(structure);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getCargo(boolean hasCargo) {
        when(gameState.getUnits()).then(_ -> Stream.of(unit));
        when(unit.getLocation()).thenReturn(hasCargo ? fixture : tile);
        assertThat(fixture.getCargo()).isEqualTo(hasCargo ? unit : null);
    }

    @Test
    void getHangar(SeededRng random) {
        when(unitType.getHangar()).thenReturn(random.get());
        assertThat(fixture.getHangar()).isEqualTo(unitType.getHangar());
    }

    @Test
    void getSpeed(SeededRng random) {
        var speed = random.nextIntNotNegative();
        when(gameState.evaluate(new SpeedQuery(fixture))).thenReturn(speed);
        assertThat(fixture.getSpeed()).isEqualTo(speed);
    }

    @Test
    void getActionThisTurn(SeededRng random) {
        var foo = random.<Name>get();
        var bar = random.<Name>get();
        assertThat(fixture.setHistory(List.of(foo, bar))).isEqualTo(fixture);
        assertThat(fixture.getHistory()).containsExactly(foo, bar);
        assertThat(unitData.getHistory()).isEqualTo(new History(List.of(foo, bar)));
        unitData.setHistory(random.get());
        assertThat(fixture.getHistory()).isEqualTo(unitData.getHistory().names());
    }

    @Test
    void vision(SeededRng random) {
        var range = random.nextIntNotNegative();
        when(gameState.evaluate(new VisionRangeQuery(fixture))).thenReturn(range);
        assertThat(fixture.getVision()).isEqualTo(range);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void canSee(boolean value) {
        when(gameState.evaluate(new VisionCheckQuery(fixture, anotherTile))).thenReturn(value);
        assertThat(fixture.canSee(anotherTile)).isEqualTo(value);
    }

    @Test
    void owner(SeededRng random) {
        var playerId = random.<PlayerId>get();
        when(gameState.getPlayer(playerId)).thenReturn(player);
        unitData.setOwner(playerId);
        assertThat(fixture.getOwner()).isEqualTo(player);
    }

    @Test
    void rules(SeededRng random) {
        when(player.rules()).then(_ -> Stream.of(rule));
        var playerId = random.<PlayerId>get();
        when(gameState.getPlayer(playerId)).thenReturn(player);
        unitData.setOwner(playerId);
        assertThat(fixture.rules()).containsExactly(rule);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1,1
            10,10
            50,50
            99,99
            100,100
            101,100
            200,100
            -1,0
            -100,0
            """)
    void hp(int hp, int actualHp) {
        assertThat(fixture.setHp(hp)).isSameAs(fixture);
        assertThat(fixture.getHp()).isEqualTo(actualHp);
        assertThat(unitData.getHp()).isEqualTo(actualHp);
    }

    @Test
    void getAction() {
        when(unitType.getAbilities()).thenReturn(List.of(ability));
        when(ruleset.getCommonUnitActions()).thenReturn(List.of(commonAction));
        assertThat(fixture.getAction()).containsExactly(ability, commonAction);
    }

    @Test
    void getAction_WhenTheUnitHasAWeapon_ItIsIncluded() {
        when(unitType.getWeapons()).thenReturn(List.of(weapon));
        when(unitType.getAbilities()).thenReturn(List.of(ability));
        when(ruleset.getCommonUnitActions()).thenReturn(List.of(commonAction));
        assertThat(fixture.getAction()).containsExactly(weapon, ability, commonAction);
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isFriend(@Nullable Boolean onSameTeam, SeededRng random) {
        if (onSameTeam != null) {
            var playerId = random.<PlayerId>get();
            unitData.setOwner(playerId);
            when(gameState.getPlayer(playerId)).thenReturn(player);
            when(player.isFriend(unit)).thenReturn(onSameTeam);
        }
        assertThat(fixture.isFriend(unit)).isEqualTo(Boolean.TRUE.equals(onSameTeam));
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isFoe(@Nullable Boolean onDifferentTeams, SeededRng random) {
        if (onDifferentTeams != null) {
            var playerId = random.<PlayerId>get();
            unitData.setOwner(playerId);
            when(gameState.getPlayer(playerId)).thenReturn(player);
            when(player.isFoe(unit)).thenReturn(onDifferentTeams);
        }
        assertThat(fixture.isFoe(unit)).isEqualTo(Boolean.TRUE.equals(onDifferentTeams));
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isSelf(@Nullable Boolean haveSameOwner, SeededRng random) {
        if (haveSameOwner != null) {
            var playerId = random.<PlayerId>get();
            unitData.setOwner(playerId);
            when(gameState.getPlayer(playerId)).thenReturn(player);
            when(player.isSelf(unit)).thenReturn(haveSameOwner);
        }
        assertThat(fixture.isSelf(unit)).isEqualTo(Boolean.TRUE.equals(haveSameOwner));
    }

    @Test
    void testToString() {
        var template = "Unit[id=%s, type=%s]";
        assertThat(fixture).hasToString(template, unitData.getId().unitId(), unitType);
    }

}
