package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Physical;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class TargetListValidatorTest {

    @Test
    void instance() {
        var expected = new TargetListValidatorImpl(List.of(
                new TargetValidatorWrapper<>(new TargetValidatorForIff(), Element.class, Action.class, Element.class),
                new TargetValidatorWrapper<>(new TargetValidatorForRange(), Physical.class, Action.class, Physical.class),
                new TargetValidatorWrapper<>(new TargetValidatorForVision(), Element.class, Action.class, Asset.class),
                new TargetValidatorWrapper<>(new TargetValidatorForWeapon(), Unit.class, Weapon.class, Asset.class)
        ));
        assertThat(TargetListValidator.INSTANCE).isEqualTo(expected);
    }

}
