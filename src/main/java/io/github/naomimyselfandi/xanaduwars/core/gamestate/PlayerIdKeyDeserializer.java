package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

class PlayerIdKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext context) {
        return new PlayerId(Integer.parseInt(key));
    }

}
