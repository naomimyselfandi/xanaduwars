package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import io.github.naomimyselfandi.xanaduwars.core.model.Player;
import io.github.naomimyselfandi.xanaduwars.core.model.Version;
import io.github.naomimyselfandi.xanaduwars.core.service.Snapshot;
import io.github.naomimyselfandi.xanaduwars.core.service.SnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
class SnapshotServiceImpl implements SnapshotService {

    private final ObjectMapper objectMapper;

    @Override
    public GameState load(Snapshot snapshot) {
        record Intermediate(
                Version version,
                boolean redacted,
                int width,
                int height,
                List<PlayerImpl> players,
                ArrayNode tiles,
                int turn
        ) {}
        try {
            var intermediate = objectMapper.readValue(snapshot.json(), Intermediate.class);
            for (var i = 0; i < intermediate.players.size(); i++) {
                intermediate.players.get(i).setPosition(i);
            }
            var tiles = IntStream
                    .range(0, intermediate.height)
                    .boxed()
                    .flatMap(y -> IntStream
                            .range(0, intermediate.width)
                            .mapToObj(x -> new TileImpl().setX(x).setY(y)))
                    .toList();
            var gameState = new GameStateImpl(
                    intermediate.version,
                    intermediate.redacted,
                    intermediate.width,
                    intermediate.height,
                    intermediate.players,
                    tiles,
                    intermediate.turn
            );
            var module = new SimpleModule().addDeserializer(Player.class, new PositionDeserializer(gameState));
            var mapper = objectMapper.copy().registerModule(module);
            for (var i = 0; i < tiles.size(); i++) {
                mapper.readerForUpdating(tiles.get(i)).readValue(intermediate.tiles.get(i));
            }
            return gameState.initialize();
        } catch (IOException | RuntimeException e) {
            log.error("Failed loading game state snapshot!", e);
            throw new RuntimeException("Failed loading game state snapshot!", e);
        }
    }

    @Override
    public Snapshot save(GameState gameState) {
        try {
            return new Snapshot(objectMapper.writeValueAsString(gameState));
        } catch (IOException | RuntimeException e) {
            log.error("Failed saving game state snapshot!", e);
            throw new RuntimeException("Failed saving game state snapshot!", e);
        }
    }

}
