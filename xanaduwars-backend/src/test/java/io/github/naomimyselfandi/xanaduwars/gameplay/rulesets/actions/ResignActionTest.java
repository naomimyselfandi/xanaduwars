package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ResignActionTest {

    @Mock
    private Player player;

    private ResignAction fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new ResignAction(random.nextName());
    }

    @Test
    void execute() {
        assertThat(fixture.execute(player, None.NONE)).isEqualTo(Execution.SUCCESSFUL);
        verify(player).defeat();
    }

}
