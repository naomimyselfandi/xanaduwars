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
        when(ruleset.unitType(unitData.type())).thenReturn(unitType);
        fixture = new UnitImpl(unitData, gameState, ruleset);
    }

    @Test
    void id() {
        assertThat(fixture.id()).isEqualTo(unitData.id());
    }

    @Test
    void type() {
        assertThat(fixture.type()).isEqualTo(unitType);
    }

    @Test
    void tags(SeededRng random) {
        when(unitType.tags()).thenReturn(Set.of(random.get(), random.get()));
        assertThat(fixture.tags()).isEqualTo(unitType.tags());
    }

    @Test
    void location_Tile(SeededRng random) {
        var tileId = random.<TileId>get();
        when(tile.id()).thenReturn(tileId);
        when(gameState.tile(tileId)).thenReturn(tile);
        assertThat(fixture.location(tile)).isSameAs(fixture);
        assertThat(fixture.location()).isEqualTo(tile);
        assertThat(fixture.tile()).isEqualTo(tile);
        assertThat(unitData.location()).isEqualTo(tileId);
        var distance = random.nextInt();
        when(tile.distance(unit)).thenReturn((double) distance);
        assertThat(fixture.distance(unit)).isEqualTo(distance);
    }

    @Test
    void location_Unit(SeededRng random) {
        var unitId = random.<UnitId>get();
        when(unit.id()).thenReturn(unitId);
        when(gameState.unit(unitId)).thenReturn(unit);
        assertThat(fixture.location(unit)).isSameAs(fixture);
        assertThat(fixture.location()).isEqualTo(unit);
        assertThat(fixture.tile()).isNull();
        assertThat(unitData.location()).isEqualTo(unitId);
        assertThat(fixture.distance(tile)).isNaN();
    }

    @Test
    void terrain_WhenTheUnitIsInAnotherUnit_ThenNull(SeededRng random) {
        var unitId = random.<UnitId>get();
        when(unit.id()).thenReturn(unitId);
        when(gameState.unit(unitId)).thenReturn(unit);
        assertThat(fixture.location(unit)).isSameAs(fixture);
        assertThat(fixture.terrain()).isNull();
    }

    @Test
    void terrain_WhenTheUnitIsOnATileWithNoStructure_ThenReturnsTheTile(SeededRng random) {
        var tileId = random.<TileId>get();
        when(tile.id()).thenReturn(tileId);
        when(gameState.tile(tileId)).thenReturn(tile);
        when(tile.structure()).thenReturn(null);
        assertThat(fixture.location(tile)).isSameAs(fixture);
        assertThat(fixture.terrain()).isEqualTo(tile);
    }

    @Test
    void terrain_WhenTheUnitIsOnATileWithAStructure_ThenReturnsTheStructure(SeededRng random) {
        var tileId = random.<TileId>get();
        when(tile.id()).thenReturn(tileId);
        when(gameState.tile(tileId)).thenReturn(tile);
        when(tile.structure()).thenReturn(structure);
        assertThat(fixture.location(tile)).isSameAs(fixture);
        assertThat(fixture.terrain()).isEqualTo(structure);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void cargo(boolean hasCargo) {
        when(gameState.units()).then(_ -> Stream.of(unit));
        when(unit.location()).thenReturn(hasCargo ? fixture : tile);
        assertThat(fixture.cargo()).isEqualTo(hasCargo ? unit : null);
    }

    @Test
    void hangar(SeededRng random) {
        when(unitType.hangar()).thenReturn(random.get());
        assertThat(fixture.hangar()).isEqualTo(unitType.hangar());
    }

    @Test
    void speed(SeededRng random) {
        var speed = random.nextIntNotNegative();
        when(gameState.evaluate(new SpeedQuery(fixture))).thenReturn(speed);
        assertThat(fixture.speed()).isEqualTo(speed);
    }

    @Test
    void actionsThisTurn(SeededRng random) {
        var foo = random.<Name>get();
        var bar = random.<Name>get();
        assertThat(fixture.actionsThisTurn(List.of(foo, bar))).isEqualTo(fixture);
        assertThat(fixture.actionsThisTurn()).containsExactly(foo, bar);
        assertThat(unitData.history()).isEqualTo(new History(List.of(foo, bar)));
        unitData.history(random.get());
        assertThat(fixture.actionsThisTurn()).isEqualTo(unitData.history().names());
    }

    @Test
    void vision(SeededRng random) {
        var range = random.nextIntNotNegative();
        when(gameState.evaluate(new VisionRangeQuery(fixture))).thenReturn(range);
        assertThat(fixture.vision()).isEqualTo(range);
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
        when(gameState.player(playerId)).thenReturn(player);
        unitData.owner(playerId);
        assertThat(fixture.owner()).isEqualTo(player);
    }

    @Test
    void rules(SeededRng random) {
        when(player.rules()).then(_ -> Stream.of(rule));
        var playerId = random.<PlayerId>get();
        when(gameState.player(playerId)).thenReturn(player);
        unitData.owner(playerId);
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
        assertThat(fixture.hp(hp)).isSameAs(fixture);
        assertThat(fixture.hp()).isEqualTo(actualHp);
        assertThat(unitData.hp()).isEqualTo(actualHp);
    }

    @Test
    void actions() {
        when(unitType.abilities()).thenReturn(List.of(ability));
        when(ruleset.commonUnitActions()).thenReturn(List.of(commonAction));
        assertThat(fixture.actions()).containsExactly(ability, commonAction);
    }

    @Test
    void actions_WhenTheUnitHasAWeapon_ItIsIncluded() {
        when(unitType.weapons()).thenReturn(List.of(weapon));
        when(unitType.abilities()).thenReturn(List.of(ability));
        when(ruleset.commonUnitActions()).thenReturn(List.of(commonAction));
        assertThat(fixture.actions()).containsExactly(weapon, ability, commonAction);
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isFriend(@Nullable Boolean onSameTeam, SeededRng random) {
        if (onSameTeam != null) {
            var playerId = random.<PlayerId>get();
            unitData.owner(playerId);
            when(gameState.player(playerId)).thenReturn(player);
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
            unitData.owner(playerId);
            when(gameState.player(playerId)).thenReturn(player);
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
            unitData.owner(playerId);
            when(gameState.player(playerId)).thenReturn(player);
            when(player.isSelf(unit)).thenReturn(haveSameOwner);
        }
        assertThat(fixture.isSelf(unit)).isEqualTo(Boolean.TRUE.equals(haveSameOwner));
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("Unit[id=%s, type=%s]", unitData.id().unitId(), unitType);
    }

}
