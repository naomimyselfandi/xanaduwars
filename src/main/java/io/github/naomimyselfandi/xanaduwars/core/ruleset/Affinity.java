package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// An affinity a commander can have for some spells. This has no inherent
/// effect, but game rules may read affinities and interact with them.
public record Affinity(@JsonValue @NotNull @Valid Name name) {

    @Override
    public String toString() {
        return name.toString();
    }

}
