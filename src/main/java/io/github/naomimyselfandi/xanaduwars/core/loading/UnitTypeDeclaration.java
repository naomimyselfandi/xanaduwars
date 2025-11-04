package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.util.JsonImmutableList;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.PACKAGE)
@Getter(onMethod_ = @Override)
@JsonIgnoreProperties("omitBuildAbility")
final class UnitTypeDeclaration extends AbstractSpecification implements UnitType {

    private int speed, perception, supplyCost, aetherCost, maxHp;

    @JsonImmutableList
    private @NotNull List<UnitTag> tags = List.of();

    private @NotNull UnitSelector hangar = UnitSelectorPlaceholder.NONE;

    @JsonImmutableList
    private @NotNull List<Ability> abilities = List.of();

    @JsonImmutableList
    private @NotNull List<Weapon> weapons = List.of();

    @Override
    public boolean test(Unit unit) {
        return equals(unit.getType());
    }

    @Override
    public boolean test(UnitType unitType) {
        return equals(unitType);
    }

}
