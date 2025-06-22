package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.common.StructureTag;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Structure;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

import java.util.Set;

/// A query that calculates a structure's tag set.
public record StructureTagQuery(Structure subject) implements Query<Set<StructureTag>> {

    @Override
    public Set<StructureTag> defaultValue() {
        return subject.getType().getTags();
    }

}
