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
    public GameState getGameState() {
        return tile.getGameState();
    }

    @Override
    public StructureType getType() {
        return ruleset.getStructureType(data.getType());
    }

    @Override
    public Set<? extends Tag> getTags() {
        return getType().getTags();
    }

    @Override
    public boolean isComplete() {
        return data.isComplete();
    }

    @Override
    public Structure setComplete(boolean complete) {
        data.setComplete(complete);
        return this;
    }

    @Override
    public double getCover() {
        return getType().getCover();
    }

    @Override
    public MovementTable getMovementTable() {
        return getType().getMovementTable();
    }

    @Override
    public @Unmodifiable List<Action> getAction() {
        return List.of(ruleset.getDeploymentAction());
    }

    @Override
    public double getDistance(Physical that) {
        return tile.getDistance(that);
    }

    @Override
    public Terrain getTerrain() {
        return this;
    }

    @Override
    public String toString() {
        var id = tile.getId();
        return "Structure[x=%d, y=%d, type=%s]".formatted(id.x(), id.y(), getType());
    }

}
