package io.github.naomimyselfandi.xanaduwars.core.impl;

import io.github.naomimyselfandi.xanaduwars.core.messages.GenericEvent;
import io.github.naomimyselfandi.xanaduwars.core.messages.GetCoverQuery;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TileImplTest extends GameStateAwareTest<TileImpl> {

    private int x, y;

    @Mock
    private Tile tile;

    @Mock
    private Unit unit, anotherUnit;

    @Mock
    private TileType type, anotherType;

    @ParameterizedTest
    @CsvSource(textBlock = """
            NORTH,0,-1
            EAST,1,0
            SOUTH,0,1
            WEST,-1,0
            """)
    void step(Direction direction, int dx, int dy) {
        when(gameState.getTile(x + dx, y + dy)).thenReturn(tile);
        initializeFixture();
        assertThat(fixture.step(direction)).isEqualTo(tile);
    }

    @Test
    void getTags(SeededRng random) {
        var foo = random.<TileTag>get();
        var bar = random.not(foo);
        when(type.getTags()).thenReturn(List.of(foo, bar));
        assertThat(fixture.getTags()).containsExactly(foo, bar);
    }

    @Test
    void getCover() {
        testGetter(Tile::getCover, new GetCoverQuery(fixture));
    }

    @Test
    void getMovementTable() {
        var foo = random.<UnitTag>get();
        var bar = random.not(foo);
        var movementTable = Map.of(foo, random.nextDouble(), bar, random.nextDouble());
        when(type.getMovementTable()).thenReturn(movementTable);
        assertThat(fixture.getMovementTable()).isEqualTo(movementTable);
    }

    @Test
    void setType() {
        testSetter(Tile::getType, Tile::setType, type, anotherType, new GenericEvent(fixture));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0,0
            1,0,1
            -1,0,1
            0,1,1
            0,-1,1
            1,2,3
            -1,-2,3
            """)
    void getDistance_Tile(int dx, int dy, int expected) {
        when(tile.getX()).thenReturn(x + dx);
        when(tile.getY()).thenReturn(y + dy);
        assertThat(fixture.getDistance(tile)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0,0
            1,0,1
            -1,0,1
            0,1,1
            0,-1,1
            1,2,3
            -1,-2,3
            """)
    void getDistance_Unit(int dx, int dy, int expected) {
        when(tile.getX()).thenReturn(x + dx);
        when(tile.getY()).thenReturn(y + dy);
        when(unit.getLocation()).thenReturn(tile);
        assertThat(fixture.getDistance(unit)).isEqualTo(expected);
    }

    @Test
    void getDistance_UnitInUnit() {
        when(unit.getLocation()).thenReturn(anotherUnit);
        assertThat(fixture.getDistance(unit)).isNaN();
    }

    @Test
    void getAssociatedObjects() {
        assertThat(fixture.getAssociatedObjects()).containsExactly(type);
    }

    @Test
    void testToString(SeededRng random) {
        var name = random.nextString();
        when(type.getName()).thenReturn(name);
        assertThat(fixture).hasToString("%s(%d, %d)", name, x, y);
    }

    @Override
    TileImpl create() {
        x = random.nextInt();
        y = random.not();
        return new TileImpl().setX(x).setY(y).setType(type);
    }

}
