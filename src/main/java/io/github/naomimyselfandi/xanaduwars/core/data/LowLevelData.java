package io.github.naomimyselfandi.xanaduwars.core.data;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.NodeId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TileId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.UnitId;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/// A common base class for low-level map and game state information.
@Getter
@Setter
@Entity
@ToString
@Table(name = "ll_data")
@FieldNameConstants(asEnum = true)
@DiscriminatorColumn(name = "kind")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class LowLevelData {

    /// Primary key.
    @Id
    private @NotNull UUID id;

    /// Low-level player data, in turn order.
    @NotNull
    @ElementCollection
    @OrderBy("player_id")
    @Setter(AccessLevel.NONE)
    @CollectionTable(name = "player_data", joinColumns = @JoinColumn(name = "parent_id"))
    private List<@NotNull @Valid PlayerData> players = new ArrayList<>();

    /// Low-level tile data, in row-column order.
    @NotNull
    @ElementCollection
    @OrderBy("tile_id DESC")
    @Setter(AccessLevel.NONE)
    @CollectionTable(name = "tile_data", joinColumns = @JoinColumn(name = "parent_id"))
    private List<@NotNull @Valid TileData> tiles = new ArrayList<>();

    /// Low-level unit data. The keys into the map are the units' in-game IDs.
    @NotNull
    @ElementCollection
    @Setter(AccessLevel.NONE)
    @MapKeyColumn(name = "unit_id")
    @CollectionTable(name = "unit_data", joinColumns = @JoinColumn(name = "parent_id"))
    private SortedMap<@NotNull @Valid UnitId, @NotNull @Valid UnitData> units = new TreeMap<>();

    /// The width of the map.
    private @Positive int width;

    /// The ID to use for the next created unit.
    private @PositiveOrZero int nextUnitId;

    /// The height of the map.
    public int height() {
        return tiles.size() / width;
    }

    /// Get the tile data at the given `(x, y)` coordinates.
    /// @throws IndexOutOfBoundsException if either coordinate is negative or
    /// too large.
    public TileData tileData(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height()) {
            throw new IndexOutOfBoundsException("Invalid coordinates (%d, %d).".formatted(x, y));
        } else {
            return tiles.get(x + y * width);
        }
    }

    /// Create and add new unit data.
    public UnitData createUnitData(NodeId location, int type) {
        var conflict = switch (location) {
            case TileId l -> {
                var x = l.x(); // We could use destructuring for this,
                var y = l.y(); // but it confuses the coverage report.
                yield x < 0 || x >= width || y < 0 || y >= height()
                        || units.values().stream().anyMatch(unit -> unit.location().equals(l));
            }
            case UnitId l -> !units.containsKey(l);
        };
        if (conflict) {
            throw new IllegalStateException("Cannot create unit at location %s.".formatted(location));
        } else {
            var unitId = new UnitId(nextUnitId++);
            var unit = new UnitData().unitId(unitId).location(location).unitType(type);
            units.put(unitId, unit);
            return unit;
        }
    }

    @AssertTrue
    boolean hasValidLocations() {
        int height = height();
        return units.values().stream().map(UnitData::location).allMatch(location -> switch (location) {
            case UnitId l -> units.containsKey(l);
            case TileId l -> (l.x() < width) && (l.y() < height);
        });
    }

    @AssertTrue
    boolean hasOnlyOneUnitPerTile() {
        return units
                .values()
                .stream()
                .map(UnitData::location)
                .filter(TileId.class::isInstance)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .values()
                .stream()
                .allMatch(count -> count <= 1);
    }

    @AssertTrue
    boolean hasValidPlayerIds() {
        var expected = IntStream.range(0, players.size()).mapToObj(PlayerId::new).toList();
        // Using ArrayList because toList() prohibits null.
        var actual = players.stream().map(PlayerData::playerId).collect(Collectors.toCollection(ArrayList::new));
        return expected.equals(actual);
    }

    @AssertTrue
    boolean hasValidTileIds() {
        var expected = IntStream
                .range(0, height())
                .boxed()
                .flatMap(y -> IntStream.range(0, width).mapToObj(x -> new TileId(x, y)))
                .toList();
        // Using ArrayList because toList() prohibits null.
        var actual = tiles.stream().map(TileData::tileId).collect(Collectors.toCollection(ArrayList::new));
        return expected.equals(actual);
    }

    @AssertTrue
    boolean hasValidUnitIds() {
        return units
                .entrySet()
                .stream()
                .allMatch(entry -> entry.getKey().equals(entry.getValue().unitId()));
    }

    @Override
    public boolean equals(Object that) {
        if (id == null) {
            return this == that;
        } else {
            return that instanceof LowLevelData it && id.equals(it.id);
        }
    }

    @Override
    public abstract int hashCode();

}
