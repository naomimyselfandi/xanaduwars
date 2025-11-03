package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ReadyStateQueryTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private Unit actor;

    @InjectMocks
    private ReadyStateQuery fixture;

    @Test
    void defaultValue() {
        assertThat(fixture.defaultValue(runtime)).isTrue();
        verifyNoInteractions(runtime, actor);
    }

}
