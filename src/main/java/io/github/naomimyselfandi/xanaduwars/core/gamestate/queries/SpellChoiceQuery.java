package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Event;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A query that tests whether a commander can choose some spells.
public record SpellChoiceQuery(Commander commander, @Unmodifiable List<Spell> spells) implements Event<Result> {

    /// An event indicating a player has chosen their commander and spells.
    /// @implSpec This constructor takes an immutable copy of its list argument.
    public SpellChoiceQuery(Commander commander, List<Spell> spells) {
        this.commander = commander;
        this.spells = List.copyOf(spells);
    }

    @Override
    public Result defaultValue() {
        if (spells.stream().anyMatch(Spell::isSignatureSpell)) {
            return Result.fail("Cannot choose signature spells.");
        } else if (spells.stream().distinct().count() != spells.size()) {
            return Result.fail("Cannot choose the same spell more than once.");
        } else if (spells.size() != commander.getChosenSpells()) {
            return Result.fail("Must choose exactly %d spells.".formatted(commander.getChosenSpells()));
        } else {
            return Result.okay();
        }
    }

}
