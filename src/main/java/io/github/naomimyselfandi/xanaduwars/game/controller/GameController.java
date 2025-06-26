package io.github.naomimyselfandi.xanaduwars.game.controller;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.auth.Authenticated;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.GameStateDto;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.VersionService;
import io.github.naomimyselfandi.xanaduwars.game.dto.GameDto;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.service.GameService;
import io.github.naomimyselfandi.xanaduwars.map.dto.MapDto;
import io.github.naomimyselfandi.xanaduwars.util.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final VersionService versionService;

    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getMetadata(@PathVariable Id<Game> id) throws NoSuchEntityException {
        return ResponseEntity.ok(gameService.getMetadata(id));
    }

    @GetMapping("/{id}/choices")
    public ResponseEntity<ChoiceDto> getChoices(
            @PathVariable Id<Game> id,
            @Authenticated Id<Account> accountId
    ) throws NoSuchEntityException, ForbiddenOperationException {
        var playerId = gameService.getPlayerId(id, accountId);
        return ResponseEntity.ok(gameService.getChoices(id, playerId));
    }

    @PutMapping("/{id}/choices")
    public ResponseEntity<ChoiceDto> setChoices(
            @PathVariable Id<Game> id,
            @Authenticated Id<Account> accountId,
            @RequestBody ChoiceDto choices
    ) throws NoSuchEntityException, ForbiddenOperationException, InvalidOperationException, ConflictException {
        var playerId = gameService.getPlayerId(id, accountId);
        return ResponseEntity.ok(gameService.setChoices(id, playerId, choices));
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<GameStateDto> getState(
            @PathVariable Id<Game> id,
            @Authenticated Id<Account> accountId,
            @RequestParam(required = false, name = "playerId") @Nullable Integer rawPlayerId
    ) throws NoSuchEntityException, ConflictException {
        var playerId = switch (rawPlayerId) {
            case Integer i when i < 0 -> null;
            case Integer i -> new PlayerId(i);
            case null -> gameService.findPlayerId(id, accountId);
        };
        return ResponseEntity.ok(gameService.getState(id, playerId));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<GameDto> join(
            @PathVariable Id<Game> id,
            @Authenticated Id<Account> accountId
    ) throws NoSuchEntityException, ConflictException {
        return ResponseEntity.ok(gameService.join(id, accountId));
    }

    @PostMapping("/{id}/drop")
    public ResponseEntity<Void> drop(
            @PathVariable Id<Game> id,
            @Authenticated Id<Account> accountId
    ) throws NoSuchEntityException, ConflictException {
        gameService.drop(id, accountId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/kick")
    public ResponseEntity<GameDto> kick(
            @PathVariable Id<Game> id,
            @RequestParam Id<Account> accountId
    ) throws NoSuchEntityException, ConflictException {
        gameService.drop(id, accountId);
        return getMetadata(id);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<GameDto> start(@PathVariable Id<Game> id) throws ConflictException, NoSuchEntityException {
        return ResponseEntity.ok(gameService.start(id));
    }

    // Proper map support is coming later; for now, specifying the map data inline works.

    @PostMapping("/withAdHocMap")
    public ResponseEntity<GameDto> createAdHoc(
            @Authenticated Id<Account> accountId,
            @RequestBody MapDto map,
            @RequestParam(required = false) @Nullable Version version
    ) {
        if (version == null) version = versionService.current();
        return ResponseEntity.ok(gameService.createAdHoc(accountId, map, version));
    }

}
