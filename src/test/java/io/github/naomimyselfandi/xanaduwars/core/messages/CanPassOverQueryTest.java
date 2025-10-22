package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CanPassOverQueryTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private Unit traveller, obstacle;

    private CanPassOverQuery fixture;

    @BeforeEach
    void setup() {
        fixture = new CanPassOverQuery(traveller, obstacle);
    }

    @Test
    void defaultValue() {
        assertThat(fixture.defaultValue(runtime)).isTrue();
        verifyNoInteractions(runtime, traveller, obstacle);
    }

}
