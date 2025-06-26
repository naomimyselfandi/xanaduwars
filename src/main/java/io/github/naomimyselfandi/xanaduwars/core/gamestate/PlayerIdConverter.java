package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
class PlayerIdConverter implements Converter<String, PlayerId> {

    @Override
    public PlayerId convert(String source) {
        return new PlayerId(Integer.parseInt(source));
    }

}
