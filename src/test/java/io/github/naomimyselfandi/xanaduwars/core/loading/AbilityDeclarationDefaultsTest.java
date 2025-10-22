package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AbilityDeclarationDefaultsTest {

    @Mock
    private Unit actor;

    private final AbilityDeclaration fixture = new AbilityDeclaration();

    @Test
    void defaults() {
        assertThat(fixture.getTags()).isEmpty();
        assertThat(fixture.getTarget()).isEqualTo(TargetOfNothing.NOTHING);
        assertThat(fixture.getSupplyCost()).isEqualTo(Script.ZERO);
        assertThat(fixture.getAetherCost()).isEqualTo(Script.ZERO);
        assertThat(fixture.getFocusCost()).isEqualTo(Script.ZERO);
        assertThat(fixture.getFilter()).isEqualTo(Script.TRUE);
    }

    @Test
    void makeArgumentsMap() {
        var target = new Object();
        assertThat(fixture.makeArgumentMap(actor, target)).isEqualTo(Map.of("actor", actor, "target", target));
    }

}
