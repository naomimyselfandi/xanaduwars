package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.xanaduwars.core.message.ContextualRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.messages.GenericEvent;
import io.github.naomimyselfandi.xanaduwars.core.messages.GetCoverQuery;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@NoArgsConstructor
@Getter(onMethod_ = @Override)
@FieldNameConstants(asEnum = true)
final class TileImpl extends AbstractNode implements Tile {

    @JsonIgnore
    @Setter(AccessLevel.PACKAGE)
    private int x, y;

    @JsonSerialize(using = NameSerializer.class)
    private TileType type;

    @Override
    public @Nullable Tile step(Direction direction) {
        int dx = switch (direction) {
            case NORTH, SOUTH -> 0;
            case EAST -> 1;
            case WEST -> -1;
        };
        int dy = switch (direction) {
            case EAST, WEST -> 0;
            case SOUTH -> 1;
            case NORTH -> -1;
        };
        return getGameState().getTile(x + dx, y + dy);
    }

    @Override
    @JsonIgnore
    public List<TileTag> getTags() {
        return type.getTags();
    }

    @Override
    @JsonIgnore
    public double getCover() {
        return evaluate(new GetCoverQuery(this));
    }

    @Override
    @JsonIgnore
    public UnitSelectorMap<Double> getMovementTable() {
        return type.getMovementTable();
    }

    @Override
    public TileImpl setType(TileType type) {
        if (!type.equals(this.type)) {
            this.type = type;
            dispatch(new GenericEvent(this));
        }
        return this;
    }

    @Override
    public double getDistance(Node other) {
        return switch (other) {
            case Tile tile -> getDistance(tile);
            case Unit unit -> switch (unit.getLocation()) {
                case Tile tile -> getDistance(tile);
                case Unit _ -> Double.NaN;
            };
        };
    }

    @Override
    Stream<ContextualRuleSource> getAssociatedObjects() {
        return Stream.of(type);
    }

    private int getDistance(Tile tile) {
        return Math.abs(x - tile.getX()) + Math.abs(y - tile.getY());
    }

    @Override
    public String toString() {
        return "%s(%d, %d)".formatted(type.getName(), x, y);
    }

}
