package io.github.naomimyselfandi.xanaduwars.map.dto;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Team;
import lombok.Data;

/// A DTO representing a player slot in a map.
@Data
public class MapPlayerDto {
    private Team team;
}
