package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EventTest {

    @Test
    void defaultValue() {
        assertThat(new Event() {}.defaultValue()).isEqualTo(None.NONE);
    }

}

