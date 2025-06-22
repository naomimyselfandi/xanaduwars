package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Tile;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetValidatorWrapperTest {

    private TargetSpec spec;

    @Mock
    private NormalAction normalAction;

    @Mock
    private Weapon weapon;

    @Mock
    private Tile tile0, tile1;

    @Mock
    private Unit unit0, unit1;

    @Mock
    private TargetValidator<Unit, Weapon, Asset> delegate;

    private TargetValidatorWrapper<Unit, Weapon, Asset> fixture;

    @BeforeEach
    void setup(SeededRng random) {
        spec = random.get();
        fixture = new TargetValidatorWrapper<>(delegate, Unit.class, Weapon.class, Asset.class);
    }

    @Test
    void fail(SeededRng random) {
        when(delegate.fail()).thenReturn(random.get());
        assertThat(fixture.fail()).isEqualTo(delegate.fail());
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void test(boolean actorMatches, boolean actionMatches, boolean targetMatches, boolean shouldDelegate) {
        var actor = actorMatches ? unit0 : tile0;
        var action = actionMatches ? weapon : normalAction;
        var target = targetMatches ? unit1 : tile1;
        if (shouldDelegate) {
            for (var value : List.of(true, false)) {
                when(delegate.test((Unit) actor, (Weapon) action, (Asset) target, spec)).thenReturn(value);
                assertThat(fixture.test(actor, action, target, spec)).isEqualTo(value);
            }
        } else {
            assertThat(fixture.test(actor, action, target, spec)).isTrue();
            verifyNoInteractions(delegate);
        }
    }

}
