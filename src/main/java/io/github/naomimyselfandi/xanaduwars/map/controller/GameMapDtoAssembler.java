package io.github.naomimyselfandi.xanaduwars.map.controller;

import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapDto;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.security.hateoas.SecurityAwareAssembler;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import static io.github.naomimyselfandi.xanaduwars.security.hateoas.Faker.$;

@Component
@RequiredArgsConstructor
class GameMapDtoAssembler extends SecurityAwareAssembler<GameMapDto> {

    @Link
    public Object self(GameMapController controller, GameMapDto dto) {
        return controller.get(dto.id());
    }

    @Link
    public Object update(GameMapController controller, GameMapDto dto) {
        return controller.update(dto.id(), $());
    }

    @Link
    public @Nullable Object unpublish(GameMapController controller, GameMapDto dto) {
        return status(controller, dto, GameMap.Status.UNPUBLISHED);
    }

    @Link
    public @Nullable Object publish(GameMapController controller, GameMapDto dto) {
        return status(controller, dto, GameMap.Status.PUBLISHED);
    }

    @Link
    public @Nullable Object submit(GameMapController controller, GameMapDto dto) {
        return status(controller, dto, GameMap.Status.SUBMITTED);
    }

    @Link
    public @Nullable Object approve(GameMapController controller, GameMapDto dto) {
        return status(controller, dto, GameMap.Status.OFFICIAL);
    }

    private static @Nullable Object status(GameMapController controller, GameMapDto dto, GameMap.Status status) {
        return status == dto.status() ? null : controller.updateStatus(dto.id(), status);
    }

}
