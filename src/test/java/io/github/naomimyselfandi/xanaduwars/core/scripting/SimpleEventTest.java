package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SimpleEventTest {

    @Test
    void defaultValue() {
        assertThat(new BazEvent(null, mock(), mock()).defaultValue()).isEqualTo(None.NONE);
    }

}
