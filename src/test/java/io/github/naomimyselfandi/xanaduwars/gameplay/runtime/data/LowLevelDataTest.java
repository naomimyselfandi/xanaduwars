package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Direction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SuppressWarnings("EqualsWithItself")
class LowLevelDataTest {

    private static final int PLAYER_COUNT = 2;
    private static final int WIDTH = 3;
    private static final int HEIGHT = 2;
    private static final PlayerId PLAYER_ID = new PlayerId(0);
    private static final TileId TILE_ID = new TileId(0, 0);
    private static final UnitId UNIT_ID = new UnitId(0);

    private final LowLevelData fixture = new GameData()
            .activePlayer(new PlayerId(0))
            .version(new Version("0.0.0"))
            .id(UUID.randomUUID())
            .width(WIDTH);

    private UnitData unit;

    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @AfterAll
    static void clearValidator() {
        validator = null;
    }

    @BeforeEach
    void setup() {
        for (var playerId = 0; playerId < PLAYER_COUNT; playerId++) {
            var data = new PlayerData().playerId(new PlayerId(playerId)).commander(new CommanderId(0)).team(playerId);
            fixture.players().add(data);
        }
        for (var y = 0; y < HEIGHT; y++) {
            for (var x = 0; x < WIDTH; x++) {
                fixture.tiles().add(new TileData().tileId(new TileId(x, y)).tileType(new TileTypeId(0)));
            }
        }
        unit = new UnitData()
                .unitId(UNIT_ID)
                .location(TILE_ID)
                .owner(PLAYER_ID)
                .unitType(new UnitTypeId(1));
        fixture.units().put(UNIT_ID, unit);
        for (var offset = 1; offset <= 2; offset++) {
            var unitId = new UnitId(UNIT_ID.intValue() + offset);
            var cargo = new UnitData()
                    .unitId(unitId)
                    .location(UNIT_ID)
                    .owner(PLAYER_ID)
                    .unitType(new UnitTypeId(0));
            fixture.units().put(unitId, cargo);
        }
        fixture.nextUnitId(fixture.units().size());
    }

    @Test
    void height() {
        assertThat(fixture.height()).isEqualTo(HEIGHT);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0,0
            1,0,1
            2,0,2
            0,1,3
            1,1,4
            2,1,5
            """)
    void tileData(int x, int y, int index) {
        assertThat(fixture.tileData(x, y)).isEqualTo(fixture.tiles().get(index));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            -1,0
            0,-1
            3,0
            0,2
            """)
    void tileData_WhenACoordinateIsOutOfRange_ThenThrows(int x, int y) {
        assertThatThrownBy(() -> fixture.tileData(x, y))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("Invalid coordinates (%d, %d).", x, y);
    }

    @MethodSource
    @ParameterizedTest
    void createUnitData(NodeId location, UnitTypeId type) {
        var nextUnitId = fixture.nextUnitId();
        var unitIds = new HashSet<>(fixture.units().keySet());
        var created = fixture.createUnitData(location, type);
        unitIds.add(created.unitId());
        for (var field : UnitData.Fields.values()) {
            var _ = switch (field) {
                case unitId -> {
                    assertThat(fixture.units().get(created.unitId())).isSameAs(created);
                    assertThat(fixture.units().keySet()).isEqualTo(unitIds);
                    assertThat(created.unitId()).isEqualTo(new UnitId(nextUnitId));
                    assertThat(fixture.nextUnitId()).isEqualTo(nextUnitId + 1);
                    yield null;
                }
                case unitType -> assertThat(created.unitType()).isEqualTo(type);
                case owner -> {
                    assertThat(created.owner()).isNull();
                    yield null;
                }
                case hitpoints -> assertThat(created.hitpoints()).isEqualTo(Percent.FULL);
                case location -> assertThat(created.location()).isEqualTo(location);
                case canAct -> assertThat(created.canAct()).isFalse();
            };
        }
    }

    private static Stream<Arguments> createUnitData() {
        return Stream.of(
                arguments(new TileId(WIDTH - 1, HEIGHT - 1), new UnitTypeId(1)),
                arguments(UNIT_ID, new UnitTypeId(2))
        );
    }

    @MethodSource
    @ParameterizedTest
    void createUnitData_WhenTheLocationIsInvalid_ThenThrows(NodeId location) {
        var original = Map.copyOf(fixture.units());
        assertThatThrownBy(() -> fixture.createUnitData(location, new UnitTypeId(0)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot create unit at location %s.", location);
        assertThat(fixture.units()).isEqualTo(original);
    }

    @Test
    void createUnitData_HandlesInitialUnit() {
        fixture.nextUnitId(0).units().clear();
        var created = fixture.createUnitData(TILE_ID, new UnitTypeId(0));
        assertThat(created.unitId()).isEqualTo(UNIT_ID);
        assertThat(fixture.units().get(UNIT_ID)).isSameAs(created);
        assertThat(fixture.nextUnitId()).isOne();
    }

    private static Stream<NodeId> createUnitData_WhenTheLocationIsInvalid_ThenThrows() {
        return Stream.of(
                TILE_ID,
                new TileId(-1, 0),
                new TileId(0, -1),
                new TileId(WIDTH, 0),
                new TileId(0, HEIGHT),
                new UnitId(42)
        );
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0
            1,0
            2,0
            0,1
            1,1
            2,1
            """)
    void validGameDataPassesValidation(int x, int y) {
        unit.location(new TileId(x, y));
        assertThat(validator.validate(fixture)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void playerWithWrongIdFailsValidation(int index) {
        fixture.players().get(index).playerId(new PlayerId(index + 1));
        assertThat(validator.validate(fixture))
                .singleElement()
                .extracting(ConstraintViolation::getPropertyPath)
                .hasToString("validPlayerIds");
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0
            1,0
            2,0
            0,1
            1,1
            2,1
            """)
    void missingTileFailsValidation(int x, int y) {
        fixture.tiles().removeIf(tile -> tile.tileId().equals(new TileId(x, y)));
        assertThat(validator.validate(fixture))
                .singleElement()
                .extracting(ConstraintViolation::getPropertyPath)
                .hasToString("validTileIds");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void missingRowFailsValidation(int missingRow) {
        fixture.tiles()
                .stream()
                .filter(tile -> tile.tileId().y() == missingRow)
                .forEach(tile -> tile.tileId(new TileId(tile.tileId().x(), HEIGHT)));
        assertThat(validator.validate(fixture))
                .singleElement()
                .extracting(ConstraintViolation::getPropertyPath)
                .hasToString("validTileIds");
    }

    @Test
    void incorrectlyOrderedTilesFailValidation() {
        var correct = List.copyOf(fixture.tiles());
        do {
            Collections.shuffle(fixture.tiles());
        } while (fixture.tiles().equals(correct));
        assertThat(validator.validate(fixture))
                .singleElement()
                .extracting(ConstraintViolation::getPropertyPath)
                .hasToString("validTileIds");
    }

    @Test
    void unitWithWrongIdFailsValidation() {
        unit.unitId(new UnitId(42));
        assertThat(validator.validate(fixture))
                .singleElement()
                .extracting(ConstraintViolation::getPropertyPath)
                .hasToString("validUnitIds");
    }

    @EnumSource
    @ParameterizedTest
    void unitOffMapFailsValidation(Direction direction) {
        unit.location(switch (direction) {
            case NORTH -> new TileId(0, -1);
            case EAST -> new TileId(WIDTH + 1, 0);
            case SOUTH -> new TileId(0, HEIGHT + 1);
            case WEST -> new TileId(-1, 0);
        });
        assertThat(validator.validate(fixture))
                .singleElement()
                .extracting(ConstraintViolation::getPropertyPath)
                .hasToString(switch (direction) {
                    case NORTH -> "units[%s].location.y".formatted(unit.unitId());
                    case EAST, SOUTH -> "validLocations";
                    case WEST -> "units[%s].location.x".formatted(unit.unitId());
                });
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0
            1,0
            2,0
            0,1
            1,1
            2,1
            """)
    void unitsOnSameTileFailValidation(int x, int y) {
        var location = new TileId(x, y);
        unit.location(location);
        var unitId = new UnitId(fixture.units().size() + 1);
        var otherUnit = new UnitData()
                .unitId(unitId)
                .location(location)
                .owner(PLAYER_ID)
                .unitType(new UnitTypeId(0));
        fixture.units().put(unitId, otherUnit);
        assertThat(validator.validate(fixture))
                .singleElement()
                .extracting(ConstraintViolation::getPropertyPath)
                .hasToString("onlyOneUnitPerTile");
    }

    @Test
    void unitInDestroyedTransportFailsValidation() {
        fixture.units().remove(UNIT_ID);
        assertThat(validator.validate(fixture))
                .singleElement()
                .extracting(ConstraintViolation::getPropertyPath)
                .hasToString("validLocations");
    }

    @Test
    void testEquals() {
        assertThat(fixture).isNotEqualTo(new Object());
        assertThat(fixture).isNotEqualTo(null);
        var other = new GameData();
        assertThat(fixture).isEqualTo(fixture);
        assertThat(fixture).isNotEqualTo(other);
        assertThat(other).isEqualTo(other);
        assertThat(other).isNotEqualTo(fixture);
        assertThat(other.id(UUID.randomUUID())).isSameAs(other);
        assertThat(fixture).isEqualTo(fixture);
        assertThat(fixture).isNotEqualTo(other);
        assertThat(other.id(fixture.id())).isSameAs(other);
        assertThat(fixture).isEqualTo(other);
    }

}
