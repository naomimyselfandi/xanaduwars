package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameplayExceptionTest {

    @Test
    void hasNoStackTrace() {
        assertThat(new GameplayException("").getStackTrace()).isEmpty();
    }

}
