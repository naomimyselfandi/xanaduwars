package io.github.naomimyselfandi.xanaduwars.core.impl;

import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.service.GameStateFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
class GameStateFactoryImpl implements GameStateFactory {

    @Override
    public GameState create(int width, int height, int playerCount, Version version) {
        var commander = version
                .getDeclarations()
                .filter(Commander.class::isInstance)
                .map(Commander.class::cast)
                .findFirst()
                .orElseThrow();
        var spells = commander.getSignatureSpells();
        var tileType = version
                .getDeclarations()
                .filter(TileType.class::isInstance)
                .map(TileType.class::cast)
                .findFirst()
                .orElseThrow();
        var players = IntStream
                .range(0, playerCount)
                .mapToObj(i -> new PlayerImpl()
                        .setPosition(i)
                        .setTeam(i)
                        .setCommander(commander)
                        .setAbilities(spells))
                .toList();
        var tiles = IntStream
                .range(0, height)
                .boxed()
                .flatMap(y -> IntStream.range(0, width).mapToObj(x -> new TileImpl()
                        .setX(x)
                        .setY(y)
                        .setType(tileType))
                ).toList();
        return new GameStateImpl(
                version,
                false,
                width,
                height,
                players,
                tiles,
                0
        ).initialize();
    }

}
