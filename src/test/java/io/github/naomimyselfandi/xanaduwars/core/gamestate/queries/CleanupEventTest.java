package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CleanupEventTest {

    @Test
    void subject() {
        assertThat(new CleanupEvent().subject()).isNull();
    }

}
