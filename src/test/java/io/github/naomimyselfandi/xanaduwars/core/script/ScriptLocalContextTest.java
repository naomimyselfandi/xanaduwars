package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ScriptLocalContextTest {

    @Mock
    private EvaluationContext parent;

    @InjectMocks
    private ScriptLocalContext fixture;

    @Test
    void lookupMissingVariable(SeededRng random) {
        var name = random.nextString();
        var value = random.nextString();
        when(parent.lookupVariable(name)).thenReturn(value);
        assertThat(fixture.lookupMissingVariable(name)).isEqualTo(value);
    }

}
