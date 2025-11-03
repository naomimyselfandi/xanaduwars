package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Ability;
import io.github.naomimyselfandi.xanaduwars.core.model.Commander;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;

import java.util.List;

/// A query that validates a player's spell choices. This query intentionally
/// allows fewer spells to be selected than the maximum.
public record AreSpellChoicesOkQuery(Commander commander, List<Ability> choices) implements SimpleQuery<Boolean> {

    /// A query that validates a player's spell choices.
    public AreSpellChoicesOkQuery(Commander commander, List<Ability> choices) {
        this.commander = commander;
        this.choices = List.copyOf(choices);
    }

    @Override
    public Boolean defaultValue(ScriptRuntime runtime) {
        return (choices.size() <= commander.getChosenSpells())
                && (choices.size() == choices.stream().distinct().count())
                && (choices.stream().allMatch(Ability::isSpellChoice));
    }

}
