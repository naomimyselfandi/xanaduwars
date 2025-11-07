package io.github.naomimyselfandi.xanaduwars.map.dto;

import io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import lombok.Builder;

import java.util.List;

/// A DTO representing a game map.
@NotCovered // Trivial
@Builder(toBuilder = true)
public record GameMapDto(
        Id<GameMap> id,
        String name,
        GameMap.Status status,
        BaseAccountDto author,
        int width,
        int height,
        List<MapTileDto> tiles,
        List<PlayerSlotDto> playerSlots
) {}
