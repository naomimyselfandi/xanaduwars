package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.AttackEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.AttackStage;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.CounterattackValidation;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A rule that allows units to counterattack.
public record AttackCounterRule() implements GameRule<AttackEvent, None> {

    @Override
    public boolean handles(AttackEvent query, None value) {
        var attacker = query.subject();
        return query.stage() == AttackStage.MAIN && query.target() instanceof Unit defender
                && attacker.gameState().evaluate(new CounterattackValidation(attacker, defender));
    }

    @Override
    public None handle(AttackEvent query, None value) {
        var attacker = query.subject();
        var defender = (Unit) query.target();
        attacker.gameState().evaluate(AttackEvent.of(defender, attacker, AttackStage.COUNTER));
        return None.NONE;
    }

}
