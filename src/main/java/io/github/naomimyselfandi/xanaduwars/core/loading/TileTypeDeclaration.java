package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.TileTag;
import io.github.naomimyselfandi.xanaduwars.core.model.TileType;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitSelectorMap;
import io.github.naomimyselfandi.xanaduwars.util.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.PACKAGE)
@Getter(onMethod_ = @Override)
@NotCovered // Incorrectly reported as not covered
final class TileTypeDeclaration extends AbstractSpecification implements TileType {

    private @PositiveOrZero double cover;

    private @NotNull UnitSelectorMap<@PositiveOrZero Double> movementTable = UnitSelectorMap.empty();

    @JsonImmutableList
    private @NotNull List<TileTag> tags = List.of();

}
