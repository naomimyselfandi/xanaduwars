package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Ability;
import io.github.naomimyselfandi.xanaduwars.core.model.Commander;
import io.github.naomimyselfandi.xanaduwars.core.model.AbilityTag;
import io.github.naomimyselfandi.xanaduwars.util.JsonImmutableList;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter(AccessLevel.PACKAGE)
@Getter(onMethod_ = @Override)
@NotCovered // Incorrectly reported as not covered
final class CommanderDeclaration extends AbstractSpecification implements Commander {

    @JsonImmutableList
    private @NotNull List<Ability> signatureSpells = List.of();

    @JsonImmutableList
    private @NotNull List<AbilityTag> positiveAffinities = List.of();

    @JsonImmutableList
    private @NotNull List<AbilityTag> negativeAffinities = List.of();

    private @PositiveOrZero int chosenSpells = 2;

}
