package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Element;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;

/// A query that calculates an element's tags.
public record TagsQuery(@Override Element subject) implements SubjectQuery<TagSet> {}
