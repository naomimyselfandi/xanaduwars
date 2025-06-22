package io.github.naomimyselfandi.xanaduwars.map.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/// A DTO representing a game map.
@Data
public class MapDto {

    private @NotEmpty List<@NotNull @Valid MapPlayerDto> players;
    private @NotEmpty List<@NotEmpty List<@NotNull @Valid MapTileDto>> tiles;

    @AssertTrue
    boolean isRectangular() {
        return tiles.stream().mapToInt(List::size).distinct().count() < 2;
    }

}
