package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.naomimyselfandi.xanaduwars.core.model.Ability;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitType;
import io.github.naomimyselfandi.xanaduwars.core.model.Weapon;
import io.github.naomimyselfandi.xanaduwars.util.JsonImmutableList;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.PACKAGE)
@Getter(onMethod_ = @Override)
@JsonIgnoreProperties("omitBuildAbility")
@NotCovered // Incorrectly reported as not covered
final class UnitTypeDeclaration extends AbstractSpecification implements UnitType {

    private int speed, perception, supplyCost, aetherCost, maxHp;

    @JsonImmutableList
    private @NotNull List<UnitTag> tags = List.of();

    @JsonImmutableList
    private @NotNull List<UnitTag> hangar = List.of();

    @JsonImmutableList
    private @NotNull List<Ability> abilities = List.of();

    @JsonImmutableList
    private @NotNull List<Weapon> weapons = List.of();

}
