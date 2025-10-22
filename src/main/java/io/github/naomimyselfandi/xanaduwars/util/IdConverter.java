package io.github.naomimyselfandi.xanaduwars.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class IdConverter implements Converter<String, Id<?>> {

    @Override
    public Id<?> convert(String source) {
        return new Id<>(UUID.fromString(source));
    }

}
