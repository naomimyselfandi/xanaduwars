package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import io.github.naomimyselfandi.xanaduwars.core.common.TileTag;
import io.github.naomimyselfandi.xanaduwars.core.common.TileTypeId;
import io.github.naomimyselfandi.xanaduwars.core.common.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.TileType;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryName;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.util.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@JsonCommentable
@RequiredArgsConstructor
class TileTypeImpl implements TileType {

    private final @NotNull @Valid TileTypeId id;

    private final @NotNull @Valid Name name;

    private final @NotNull Set<@NotNull @Valid TileTag> tags;

    private @NotNull Map<@NotNull @Valid UnitTag, @PositiveOrZero Double> movementTable = Map.of();

    private double cover;

    private @NotNull Map<@NotNull @Valid QueryName, @NotNull @Valid Script> handlers = Map.of();

    @Override
    public String toString() {
        return name.name();
    }

}
