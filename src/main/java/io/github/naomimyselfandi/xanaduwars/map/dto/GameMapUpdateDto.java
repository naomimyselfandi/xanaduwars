package io.github.naomimyselfandi.xanaduwars.map.dto;

import jakarta.validation.constraints.*;

import java.util.List;

/// A DTO used to update a game map.
public record GameMapUpdateDto(
        @Pattern(regexp = "^\\S+(\\s\\S+)*$")
        @Pattern(regexp = "^[\\p{L}\\p{N} _\\-!@#$%^+=.]{1,32}$")
        @NotNull String name,
        @Positive int width,
        @Positive int height,
        @NotEmpty List<MapTileDto> tiles,
        @NotEmpty List<PlayerSlotDto> playerSlots
) {

    @AssertTrue
    boolean isRectangle() {
        return tiles.size() == (width * height);
    }

}
