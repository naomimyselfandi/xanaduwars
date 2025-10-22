package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Specification;
import io.github.naomimyselfandi.xanaduwars.core.message.Rule;
import io.github.naomimyselfandi.xanaduwars.util.JsonImmutableList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter(AccessLevel.PACKAGE)
abstract class AbstractSpecification implements Specification {

    private @NotEmpty String name;

    @JsonImmutableList
    private @NotNull List<@Valid Rule> rules = List.of();

    @Override
    public Stream<Rule> getContextualRules() {
        return rules.stream();
    }

    @Override
    public String toString() {
        return "%s[name=%s]".formatted(getClass().getSimpleName(), name);
    }

}
