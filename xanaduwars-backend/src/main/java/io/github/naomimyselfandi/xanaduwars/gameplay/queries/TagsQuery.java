package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TagSet;

/// A query that calculates an element's tags.
public record TagsQuery(@Override Element subject) implements SubjectQuery<TagSet, Element> {

    @Override
    public TagSet defaultValue() {
        return subject.type().tags();
    }

}
