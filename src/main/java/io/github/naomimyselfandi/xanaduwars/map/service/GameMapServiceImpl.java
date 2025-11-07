package io.github.naomimyselfandi.xanaduwars.map.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.auth.value.UnauthorizedException;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapDto;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapUpdateDto;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMapRepository;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class GameMapServiceImpl implements GameMapService {

    private final AuthService authService;
    private final GameMapServiceHelper helper;
    private final GameMapRepository gameMapRepository;

    @Override
    @Transactional(readOnly = true)
    public GameMapDto get(Id<GameMap> id) {
        return gameMapRepository.findById(id).map(helper::convert).orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    @Transactional
    public GameMapDto create(GameMapUpdateDto request) {
        var author = authService
                .loadForAuthenticatedUser()
                .map(UserDetailsDto::id)
                .map(new Account()::setId)
                .orElseThrow(UnauthorizedException::new);
        ensureNameIsAvailable(request.name());
        var map = new GameMap().setAuthor(author).setStatus(GameMap.Status.UNPUBLISHED);
        helper.update(map, request);
        var saved = gameMapRepository.save(map);
        return helper.convert(saved);
    }

    @Override
    @Transactional
    public GameMapDto update(Id<GameMap> id, GameMapUpdateDto update) {
        var map = gameMapRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        var oldName = map.getName();
        var newName = update.name();
        if (!oldName.equals(newName)) {
            ensureNameIsAvailable(newName);
        }
        helper.update(map, update);
        return helper.convert(map);
    }

    @Override
    @Transactional
    public GameMapDto updateStatus(Id<GameMap> id, GameMap.Status status) {
        var map = gameMapRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        map.setStatus(status);
        return helper.convert(map);
    }

    private void ensureNameIsAvailable(String name) {
        if (gameMapRepository.existsByName(name)) {
            throw new ConflictException("Map name '%s' is unavailable.".formatted(name));
        }
    }

}
