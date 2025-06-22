package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Turn;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import lombok.Data;

import java.util.List;

@Data
public class GameStateDto {
    private Version version;
    private PlayerId activePlayer;
    private Turn turn;
    private List<PlayerDto> players;
    private List<List<TileDto>> tiles;
}
