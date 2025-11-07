package io.github.naomimyselfandi.xanaduwars.map.controller;

import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapDto;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapUpdateDto;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.map.service.GameMapService;
import io.github.naomimyselfandi.xanaduwars.util.EntityModelAssembler;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class GameMapController {

    private final GameMapService gameMapService;
    private final EntityModelAssembler<GameMapDto> assembler;

    @GetMapping("/{id}")
    @PreAuthorize("@gameMapAccessPolicy.canAccess(#id)")
    public EntityModel<GameMapDto> get(@PathVariable Id<GameMap> id) {
        return assembler.toModel(gameMapService.get(id));
    }

    @PostMapping
    public EntityModel<GameMapDto> create(@RequestBody @Valid GameMapUpdateDto request) {
        return assembler.toModel(gameMapService.create(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@gameMapAccessPolicy.canUpdate(#id)")
    public EntityModel<GameMapDto> update(@PathVariable Id<GameMap> id, @RequestBody @Valid GameMapUpdateDto request) {
        return assembler.toModel(gameMapService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@gameMapAccessPolicy.canUpdateStatus(#id, #status)")
    public EntityModel<GameMapDto> updateStatus(@PathVariable Id<GameMap> id, @RequestParam GameMap.Status status) {
        return assembler.toModel(gameMapService.updateStatus(id, status));
    }

}
