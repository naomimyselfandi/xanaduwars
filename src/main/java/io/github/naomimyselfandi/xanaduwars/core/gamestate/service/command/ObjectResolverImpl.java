package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.common.Kind;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.*;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import org.springframework.stereotype.Service;

@Service
class ObjectResolverImpl implements ObjectResolver {

    @Override
    public Element resolveActor(GameState gameState, ActorRefDto reference) throws ConflictException {
        var candidate = switch (reference) {
            case PlayerRefDto it -> gameState.getPlayers().get(it.player());
            case PhysicalRefDto it -> resolve(gameState, it);
        };
        if (gameState.getActivePlayer().equals(candidate.getOwner().orElse(null))) {
            return candidate;
        } else {
            throw new ConflictException("Cannot issue commands to that object.");
        }
    }

    @Override
    public Object resolveTarget(GameState gameState, TargetRefDto reference, TargetSpec targetSpec) throws ConflictException {
        return switch (reference) {
            case PathRefDto it when targetSpec.path() -> it.path();
            case PathRefDto _ -> throw new ConflictException("Wrong kind of target.");
            case PhysicalRefDto it -> {
                var candidate = resolve(gameState, it);
                var ok = switch (candidate) {
                    case Structure _ -> targetSpec.filters().getOrDefault(Kind.STRUCTURE, false);
                    case Tile _ -> targetSpec.filters().getOrDefault(Kind.TILE, false);
                    case Unit _ -> targetSpec.filters().getOrDefault(Kind.UNIT, false);
                };
                if (ok) {
                    yield candidate;
                } else {
                    throw new ConflictException("Wrong kind of target.");
                }
            }
        };
    }

    private Physical resolve(GameState gameState, PhysicalRefDto reference) throws ConflictException {
        var tileId = new TileId(reference.x(), reference.y());
        var tile = gameState.getTiles().get(tileId);
        if (tile == null) {
            throw badReference("tile", tileId);
        } else return switch (reference) {
            case StructureReferenceDto _ -> tile
                    .getStructure()
                    .filter(gameState.getActivePlayer()::canSee)
                    .orElseThrow(() -> badReference("structure", tileId));
            case TileReferenceDto _ -> tile;
            case UnitReferenceDto _ -> tile
                    .getUnit()
                    .filter(gameState.getActivePlayer()::canSee)
                    .orElseThrow(() -> badReference("unit", tileId));
        };
    }

    private static ConflictException badReference(String kind, TileId tileId) {
        return new ConflictException("No (visible) %s at (%d, %d).".formatted(kind, tileId.x(), tileId.y()));
    }

}
