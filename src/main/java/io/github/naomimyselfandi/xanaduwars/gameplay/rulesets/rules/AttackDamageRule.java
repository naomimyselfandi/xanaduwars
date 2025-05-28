package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A rule that applies damage during an attack.
public record AttackDamageRule() implements GameRule<AttackEvent, None> {

    @Override
    public boolean handles(AttackEvent query, None value) {
        return true;
    }

    @Override
    public None handle(AttackEvent query, None value) {
        var target = query.target();
        target.hp(Percent.clamp(target.hp().doubleValue() - query.damage().doubleValue()));
        return None.NONE;
    }

}
