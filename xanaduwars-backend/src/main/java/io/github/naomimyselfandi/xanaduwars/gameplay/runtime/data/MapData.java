package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.CommanderId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.PlayerId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TileId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TileTypeId;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// Low-level data about a game map.
@Getter
@Setter
@Entity
@DiscriminatorValue("MAP")
@ToString(callSuper = true)
public class MapData extends LowLevelData {

    /// Set the player count supported by this map.
    public MapData playerCount(int playerCount) {
        var players = players();
        players.clear();
        for (var i = 0; i < playerCount; i++) {
            var player = new PlayerData().playerId(new PlayerId(i)).team(i).commander(new CommanderId(0));
            players.add(player);
        }
        return this;
    }

    /// Set this map's dimensions. This clears any existing tile and unit data.
    public MapData dimensions(int width, int height) {
        var tiles = tiles();
        tiles.clear();
        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                var tile = new TileData().tileId(new TileId(x, y)).tileType(new TileTypeId(0));
                tiles.add(tile);
            }
        }
        nextUnitId(0);
        units().clear();
        width(width);
        return this;
    }

    @Override
    public int hashCode() {
        return MapData.class.hashCode();
    }

}
