package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.ScriptingException;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ScriptImplTest {

    private interface Helper {}

    @Mock
    private Helper helper;

    @Mock
    private EvaluationContext context;

    @Mock
    private Statement body;

    private ScriptImpl fixture;

    @BeforeEach
    void setup() {
        fixture = new ScriptImpl(body);
    }

    @Test
    void run() {
        when(body.execute(context, Helper.class)).thenReturn(helper);
        assertThat(fixture.run(context, Helper.class)).isEqualTo(helper);
    }

    @Test
    void run_CanReturnNull() {
        when(body.execute(context, Helper.class)).thenReturn(null);
        assertThat(fixture.run(context, Helper.class)).isNull();
    }

    @Test
    void run_InterpretsTheProceedSentinelAsNull() {
        when(body.execute(context, Helper.class)).thenReturn(Statement.PROCEED);
        assertThat(fixture.run(context, Helper.class)).isNull();
    }

    @Test
    void run_WrapsExceptions(SeededRng random) {
        when(body.toString()).thenReturn(random.nextString());
        when(context.toString()).thenReturn(random.nextString());
        var e = new RuntimeException();
        when(body.execute(context, Helper.class)).thenThrow(e);
        assertThatThrownBy(() -> fixture.run(context, Helper.class))
                .isInstanceOf(ScriptingException.class)
                .hasMessage("Failed getting %s from %s in %s.", Helper.class, body, context)
                .hasCause(e);
    }

}
