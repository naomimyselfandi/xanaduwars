package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.xanaduwars.core.common.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.common.UnitTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.util.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/// A low-level description of a game state.
@Getter
@Setter
@Entity
@Table(name = "game_state")
public class GameStateData extends AbstractEntity<GameStateData> {

    private static final Turn TURN_0 = new Turn(0);
    private static final UnitId UNIT_ID_0 = new UnitId(0);

    /// The version in use by the game.
    @Embedded
    private @NotNull @Valid Version version;

    /// The game's current turn number.
    @Embedded
    @AttributeOverride(name = "ordinal", column = @Column(name = "turn"))
    private @NotNull @Valid Turn turn = TURN_0;

    /// A low-level description of the players in the game.
    @OrderColumn(name = "player")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "player_state", joinColumns = @JoinColumn(name = "game_state_id"))
    private @NotEmpty List<@NotNull @Valid PlayerData> players = new ArrayList<>();

    /// A low-level description of the structures in the game.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "structure_state", joinColumns = @JoinColumn(name = "game_state_id"))
    private @NotNull SortedMap<StructureId, @NotNull @Valid StructureData> structures = new TreeMap<>();

    /// A low-level description of the tiles in the game.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tile_state", joinColumns = @JoinColumn(name = "game_state_id"))
    private @NotEmpty SortedMap<TileId, @NotNull @Valid TileData> tiles = new TreeMap<>();

    /// A low-level description of the units in the game.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "unit_state", joinColumns = @JoinColumn(name = "game_state_id"))
    private @NotNull SortedMap<UnitId, @NotNull @Valid UnitData> units = new TreeMap<>();

    /// The next unused unit ID.
    @AttributeOverride(name = "unitId", column = @Column(name = "next_unit"))
    private @NotNull UnitId nextUnitId = UNIT_ID_0;

    /// Whether the active player has passed turn.
    private boolean passed;

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Map<NodeId, UnitId> unitsByLocation = new HashMap<>();

    /// Add another structure to this game.
    public StructureData createStructure(TileId tileId, StructureTypeId typeId) {
        var structureId = tileId.structureId();
        if (structures.containsKey(structureId)) {
            throw new IllegalStateException("%s already contains a structure.".formatted(tileId));
        }
        var structureData = new StructureData().setTypeId(typeId);
        structures.put(structureId, structureData);
        return structureData;
    }

    /// Remove a structure from this game.
    public void removeStructure(StructureId structureId) {
        structures.remove(structureId);
    }

    /// Add another unit to this game.
    public Map.Entry<UnitId, UnitData> createUnit(NodeId nodeId, UnitTypeId typeId) {
        if (!units.isEmpty() && nextUnitId.compareTo(units.sequencedKeySet().getLast()) <= 0) {
            nextUnitId = new UnitId(units.sequencedKeySet().getLast().unitId() + 1);
        }
        if (unitsByLocation.containsKey(nodeId)) {
            throw new IllegalStateException("%s already contains a unit.".formatted(nodeId));
        }
        var unitId = nextUnitId;
        nextUnitId = new UnitId(unitId.unitId() + 1);
        var unitData = new UnitData().setTypeId(typeId).setLocationId(nodeId);
        unitsByLocation.put(nodeId, unitId);
        units.put(unitId, unitData);
        return Map.entry(unitId, unitData);
    }

    /// Remove a unit from this game.
    public void removeUnit(UnitId unitId) {
        unitsByLocation.remove(units.get(unitId).getLocationId());
        units.remove(unitId);
    }

    /// Find a unit by its location.
    public Optional<UnitId> findUnitId(NodeId nodeId) {
        if (unitsByLocation.isEmpty()) {
            for (var entry : units.entrySet()) {
                unitsByLocation.put(entry.getValue().getLocationId(), entry.getKey());
            }
        }
        return Optional.ofNullable(unitsByLocation.get(nodeId));
    }

    /// Move a unit.
    public void moveUnit(UnitId unitId, NodeId nodeId) {
        if (unitsByLocation.containsKey(nodeId)) {
            throw new IllegalStateException("%s already contains a unit.".formatted(nodeId));
        }
        var unitData = units.get(unitId);
        unitsByLocation.remove(unitData.getLocationId());
        unitsByLocation.put(nodeId, unitId);
        unitData.setLocationId(nodeId);
    }

    @AssertTrue
    boolean hasValidStructureIds() {
        return structures.keySet().stream().map(StructureId::tileId).allMatch(tiles::containsKey);
    }

    @AssertTrue
    boolean hasValidTileIds() {
        if (tiles.isEmpty()) return true;
        var tileIds = tiles.sequencedKeySet();
        var lastId = tileIds.getLast();
        return IntStream
                .rangeClosed(0, lastId.y())
                .boxed()
                .flatMap(y -> IntStream.rangeClosed(0, lastId.x()).mapToObj(x -> new TileId(x, y)))
                .collect(Collectors.toSet())
                .equals(tileIds);
    }

    @AssertTrue
    boolean hasValidUnitIds() {
        return units.isEmpty() || units.sequencedKeySet().getLast().compareTo(nextUnitId) < 0;
    }

    @AssertTrue
    boolean hasValidStructureOwners() {
        return hasValidPlayerIds(structures.values(), StructureData::getPlayerId);
    }

    @AssertTrue
    boolean hasValidUnitOwners() {
        return hasValidPlayerIds(units.values(), UnitData::getPlayerId);
    }

    private <T> boolean hasValidPlayerIds(Collection<T> items, Function<T, PlayerId> getter) {
        return items
                .stream()
                .map(getter)
                .filter(Objects::nonNull)
                .mapToInt(PlayerId::playerId)
                .min()
                .orElse(-1) < players.size();
    }

}
