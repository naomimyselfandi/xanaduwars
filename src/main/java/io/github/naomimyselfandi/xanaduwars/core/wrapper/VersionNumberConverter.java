package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import jakarta.persistence.Converter;

@Converter(autoApply = true)
class VersionNumberConverter extends StringWrapperConverter<VersionNumber> {

    VersionNumberConverter() {
        super(VersionNumber::new);
    }

}
