package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Action;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionGroupValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.stream.Collectors;

/// A rule that validates action groups.
public record ActionGroupRule(
        @NotNull @Unmodifiable Set<@NotNull @Valid Name> simpleActions,
        @NotNull @Unmodifiable Set<@NotNull @Valid Name> freeActions
) implements GameRule.Validator<ActionGroupValidation> {

    /// A rule that validates action groups.
    /// @implSpec This constructor takes immutable copies of its arguments.
    public ActionGroupRule(Set<Name> simpleActions, Set<Name> freeActions) {
        this.simpleActions = Set.copyOf(simpleActions);
        this.freeActions = Set.copyOf(freeActions);
    }

    @Override
    public boolean isValid(ActionGroupValidation query) {
        return query
                .actions()
                .stream()
                .map(Action::name)
                .filter(name -> !freeActions.contains(name))
                .collect(Collectors.partitioningBy(simpleActions::contains, Collectors.counting()))
                .values()
                .stream()
                .allMatch(count -> count <= 1);
    }

}
