package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types;

import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ActionWithoutTarget;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A spell type that influences gameplay solely through its rules.
public final class PassiveSpellType extends SpellTypeImpl<None> implements ActionWithoutTarget<Player> {

    @Override
    void onCast(Player user, None target) {
        // Nothing to do.
    }

}
