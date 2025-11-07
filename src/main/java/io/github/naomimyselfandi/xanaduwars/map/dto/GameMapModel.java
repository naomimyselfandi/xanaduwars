package io.github.naomimyselfandi.xanaduwars.map.dto;

import io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.EntityModel;

import java.util.List;

/// An entity model representing a game map.
@Data
@EqualsAndHashCode(callSuper = true)
public class GameMapModel extends EntityModel<GameMapDto> {
    private Id<GameMap> id;
    private String name;
    private GameMap.Status status;
    private EntityModel<BaseAccountDto> author;
    private int width;
    private int height;
    private List<MapTileDto> tiles;
    private List<PlayerSlotDto> playerSlots;
}
