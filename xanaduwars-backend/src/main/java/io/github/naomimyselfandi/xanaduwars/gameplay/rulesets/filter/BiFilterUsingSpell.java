package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Spell;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

record BiFilterUsingSpell<S>(@Override @NotNull @Valid BiFilter<S, Spell> filter)
        implements BiFilterWrapper<S, Player> {

    @Override
    public boolean test(S subject, Player target) {
        return target.activeSpells().stream().anyMatch(spell -> filter.test(subject, spell));
    }

    @Override
    @JsonValue
    public String toString() {
        return "spell." + filter;
    }

}
