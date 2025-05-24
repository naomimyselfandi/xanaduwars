package io.github.naomimyselfandi.xanaduwars.core.filter;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.core.Element;
import io.github.naomimyselfandi.xanaduwars.core.Player;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

record BiFilterUsingOwner<S, T extends Element>(@Override @NotNull @Valid BiFilter<S, Player> filter)
        implements BiFilterWrapper<S, T> {

    @Override
    public boolean test(S subject, T target) {
        return target.owner().filter(owner -> filter.test(subject, owner)).isPresent();
    }

    @Override
    @JsonValue
    public String toString() {
        return "owner." + filter;
    }

}
