package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.ChoiceService;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.GameStateFactory;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.PlayerSlot;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.InvalidOperationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
class GameLobbyServiceImpl implements GameLobbyService {

    private final ChoiceService choiceService;
    private final PlayerIdService playerIdService;
    private final GameStateFactory gameStateFactory;
    private final AccountRepository accountRepository;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void join(Game game, Id<Account> accountId) throws ConflictException {
        doubleCheckStatus(game);
        if (playerIdService.findPlayerId(game, accountId) != null) {
            throw new ConflictException("Already in that game.");
        }
        var slots = game.getPlayerSlots();
        var playerId = IntStream
                .range(0, game.getGameStateData().getPlayers().size())
                .mapToObj(PlayerId::new)
                .filter(Predicate.not(slots::containsKey))
                .findFirst()
                .orElseThrow(() -> new ConflictException("No open player slot."));
        slots.put(playerId, new PlayerSlot().setAccount(accountRepository.findById(accountId).orElseThrow()));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @PreAuthorize("#accountId == #game.host.id || #accountId == @authService.id")
    public void drop(Game game, Id<Account> accountId) throws ConflictException {
        doubleCheckStatus(game);
        var playerId = playerIdService.findPlayerId(game, accountId);
        if (playerId == null) {
            throw new ConflictException("Not in that game.");
        }
        clear(game, playerId);
        if (game.getHost().getId().equals(accountId)) {
            game.setStatus(Game.Status.CANCELED);
        }
    }

    @Override
    @Validated
    @Transactional(propagation = Propagation.MANDATORY)
    public void move(Game game, Id<Account> accountId, @Valid PlayerId playerId) throws ConflictException {
        doubleCheckStatus(game);
        var oldPlayerId = playerIdService.findPlayerId(game, accountId);
        if (oldPlayerId == null) {
            throw new ConflictException("Not in that game.");
        }
        var newPlayerId = game.getPlayerSlots().get(playerId);
        if (newPlayerId != null) {
            throw new ConflictException("That slot is already full.");
        }
        if (playerId.playerId() >= game.getGameStateData().getPlayers().size()) {
            throw new ConflictException("Undefined slot.");
        }
        clear(game, oldPlayerId);
        var account = accountRepository.findById(accountId).orElseThrow();
        game.getPlayerSlots().put(playerId, new PlayerSlot().setAccount(account));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @PreAuthorize("@gameGuard.isPlayer(#game, #playerId)")
    public void setChoices(Game game, PlayerId playerId, ChoiceDto choices) throws InvalidOperationException {
        doubleCheckStatus(game);
        choiceService.setChoices(gameStateFactory.create(game.getGameStateData()), playerId, choices);
    }

    private static void clear(Game game, PlayerId playerId) {
        var playerData = game.getGameStateData().getPlayers().get(playerId.playerId());
        playerData.setCommanderId(null);
        playerData.getChosenSpells().setSpellIds(List.of());
        game.getPlayerSlots().remove(playerId);
    }

    private static void doubleCheckStatus(Game game) {
        if (game.getStatus() != Game.Status.PENDING) throw new IllegalStateException();
    }

}
