package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import jakarta.persistence.Converter;

@Converter(autoApply = true)
class NameConverter extends StringWrapperConverter<Name> {

    NameConverter() {
        super(Name::new);
    }

}
