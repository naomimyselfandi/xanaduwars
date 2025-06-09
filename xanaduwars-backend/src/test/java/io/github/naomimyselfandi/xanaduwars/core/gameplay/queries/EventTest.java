package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EventTest {

    private Event fixture;

    @BeforeEach
    void setup() {
        fixture = Assertions::fail;
    }

    @Test
    void defaultValue() {
        assertThat(fixture.defaultValue()).isEqualTo(None.NONE);
    }

}
