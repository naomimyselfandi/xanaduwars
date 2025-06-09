package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.StructureData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true, onParam_ = @Nullable)
final class StructureImpl extends AbstractAsset<StructureData> implements Structure {

    @Getter(onMethod_ = @Override)
    private final Tile tile;

    private final Ruleset ruleset;

    StructureImpl(StructureData data, Tile tile, Ruleset ruleset) {
        super(data);
        this.tile = tile;
        this.ruleset = ruleset;
    }

    @Override
    public GameState gameState() {
        return tile.gameState();
    }

    @Override
    public StructureType type() {
        return ruleset.structureType(data.type());
    }

    @Override
    public Set<? extends Tag> tags() {
        return type().tags();
    }

    @Override
    public boolean complete() {
        return data.complete();
    }

    @Override
    public Structure complete(boolean complete) {
        data.complete(complete);
        return this;
    }

    @Override
    public double cover() {
        return type().cover();
    }

    @Override
    public MovementTable movementTable() {
        return type().movementTable();
    }

    @Override
    public @Unmodifiable List<Action> actions() {
        return List.of(ruleset.deploymentAction());
    }

    @Override
    public double distance(Physical that) {
        return tile.distance(that);
    }

    @Override
    public Terrain terrain() {
        return this;
    }

    @Override
    public String toString() {
        var id = tile.id();
        return "Structure[x=%d, y=%d, type=%s]".formatted(id.x(), id.y(), type());
    }

}
