package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.Execution;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
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
    void setup(SeededRandom random) {
        fixture = new WaitAction(new Name("Z" + random.nextInt(Integer.MAX_VALUE)), TagSet.EMPTY);
    }

    @Test
    void execute() {
        assertThat(fixture.execute(unit, None.NONE)).isEqualTo(Execution.SUCCESSFUL);
        verifyNoInteractions(unit);
    }

}
