package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import jakarta.persistence.Converter;

@Converter(autoApply = true)
class TagConverter extends StringWrapperConverter<Tag> {

    TagConverter() {
        super(Tag::new);
    }

}
