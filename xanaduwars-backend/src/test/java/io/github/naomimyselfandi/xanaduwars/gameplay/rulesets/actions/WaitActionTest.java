package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class WaitActionTest {

    @Mock
    private Unit unit;

    private WaitAction fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new WaitAction(random.nextName());
    }

    @Test
    void execute() {
        assertThat(fixture.execute(unit, None.NONE)).isEqualTo(Execution.SUCCESSFUL);
        verifyNoInteractions(unit);
    }

}
