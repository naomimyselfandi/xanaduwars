package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import io.github.naomimyselfandi.xanaduwars.core.model.Player;

import java.io.IOException;

final class PositionDeserializer extends StdDeserializer<Player> {

    private final GameState gameState;

    PositionDeserializer(GameState gameState) {
        super(Player.class);
        this.gameState = gameState;
    }

    @Override
    public Player deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return gameState.getPlayer(parser.getIntValue());
    }

}
