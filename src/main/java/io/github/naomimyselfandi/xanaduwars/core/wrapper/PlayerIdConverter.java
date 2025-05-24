package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
class PlayerIdConverter extends IntWrapperConverter<PlayerId> implements AttributeConverter<PlayerId, Integer> {

    PlayerIdConverter() {
        super(PlayerId::new);
    }

}
