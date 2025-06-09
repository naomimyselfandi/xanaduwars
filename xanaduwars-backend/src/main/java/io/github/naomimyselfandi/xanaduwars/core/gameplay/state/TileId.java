package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import jakarta.persistence.Embeddable;
import org.hibernate.validator.constraints.Range;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/// The ID of a tile. A tile's ID is given by its coordinates.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record TileId(@Range(min = 0, max = 999) int x, @Range(min = 0, max = 999) int y) implements NodeId {

    /// Get the tile ID one step in a given direction from this one.
    public TileId step(Direction direction) {
        return switch (direction) {
            case NORTH -> new TileId(x, y - 1);
            case EAST -> new TileId(x + 1, y);
            case SOUTH -> new TileId(x, y + 1);
            case WEST -> new TileId(x - 1, y);
        };
    }

    /// Get the tile IDs within the given radius of this one. The IDs are
    /// returned in row-column order.
    public Stream<TileId> area(int radius) {
        return IntStream
                .rangeClosed(-radius, radius)
                .boxed()
                .flatMap(dy -> {
                    var width = radius - Math.abs(dy);
                    return IntStream.rangeClosed(-width, width).mapToObj(dx -> new TileId(x + dx, y + dy));
                });
    }

}
