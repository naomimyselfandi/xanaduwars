package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.naomimyselfandi.xanaduwars.core.model.Player;

import java.io.IOException;

final class PositionSerializer extends StdSerializer<Player> {

    PositionSerializer() {
        super(Player.class);
    }

    @Override
    public void serialize(Player player, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(player.getPosition());
    }

}
