package io.github.naomimyselfandi.xanaduwars.core.data;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.VersionNumber;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class GameDataFactoryImpl implements GameDataFactory {

    // Throughout this class, var _ = switch (...) is used to force the switch
    // to be a switch expression. Without the discard variable, it would be a
    // switch statement, which don't enforce exhaustiveness over enums.

    @Override
    public GameData create(GameData source) {
        return create(source.versionNumber(), source);
    }

    @Override
    public GameData create(GameMap map, VersionNumber VersionNumber) {
        return create(VersionNumber, map);
    }

    private GameData create(VersionNumber VersionNumber, LowLevelData source) {
        var id = UUID.randomUUID();
        var result = new GameData();
        for (var field : LowLevelData.Fields.values()) {
            var _ = switch (field) {
                case id -> result.id(id);
                case players -> {
                    for (var player : source.players()) {
                        result.players().add(copy(player));
                    }
                    yield null;
                }
                case tiles -> {
                    for (var tile : source.tiles()) {
                        result.tiles().add(copy(tile));
                    }
                    yield null;
                }
                case units -> {
                    for (var entry : source.units().entrySet()) {
                        result.units().put(entry.getKey(), copy(entry.getValue()));
                    }
                    yield null;
                }
                case width -> result.width(source.width());
                case nextUnitId -> result.nextUnitId(source.nextUnitId());
            };
        }
        for (var field : GameData.Fields.values()) {
            var _ = switch (field) {
                case versionNumber -> result.versionNumber(VersionNumber);
                case turn -> result.turn(source instanceof GameData s ? s.turn() : 0);
                case activePlayer -> result.activePlayer(source instanceof GameData s
                        ? s.activePlayer()
                        : new PlayerId(0));
            };
        }
        return result;
    }

    private static PlayerData copy(PlayerData source) {
        var result = new PlayerData();
        for (var field : PlayerData.Fields.values()) {
            var _ = switch (field) {
                case playerId -> result.playerId(source.playerId());
                case team -> result.team(source.team());
                case defeated -> result.defeated(source.defeated());
                case playerType -> result.playerType(source.playerType());
                case resources -> result.resources(source.resources());
                case knownSpells -> result.knownSpells(source.knownSpells());
                case activeSpells -> result.activeSpells(source.activeSpells());
            };
        }
        return result;
    }

    private static TileData copy(TileData source) {
        var result = new TileData();
        for (var field : TileData.Fields.values()) {
            var _ = switch (field) {
                case tileId -> result.tileId(source.tileId());
                case tileType -> result.tileType(source.tileType());
                case structureType -> result.structureType(source.structureType());
                case owner -> result.owner(source.owner());
                case hitpoints -> result.hitpoints(source.hitpoints());
                case construction -> result.construction(copy(source.construction()));
                case memory -> result.memory(source.memory());
            };
        }
        return result;
    }

    private static @Nullable ConstructionData copy(@Nullable ConstructionData source) {
        if (source == null) return null;
        var result = new ConstructionData();
        for (var field : ConstructionData.Fields.values()) {
            var _ = switch (field) {
                case structureType -> result.structureType(source.structureType());
                case progress -> result.progress(source.progress());
            };
        }
        return result;
    }

    private static UnitData copy(UnitData source) {
        var result = new UnitData();
        for (var field : UnitData.Fields.values()) {
            var _ = switch (field) {
                case unitId -> result.unitId(source.unitId());
                case unitType -> result.unitType(source.unitType());
                case owner -> result.owner(source.owner());
                case location -> result.location(source.location());
                case canAct -> result.canAct(source.canAct());
                case hitpoints -> result.hitpoints(source.hitpoints());
            };
        }
        return result;
    }

}
