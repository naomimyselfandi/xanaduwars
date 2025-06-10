package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ScriptImplTest {

    @Mock
    private EvaluationContext context;

    @Mock
    private Statement a, b, c;

    private ScriptImpl fixture;

    @BeforeEach
    void setup() {
        fixture = new ScriptImpl(List.of(a, b, c));
    }

    @Test
    @SneakyThrows
    void run() {
        assertThat(fixture.run(context)).isNull();
        var inOrder = inOrder(a, b, c);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
        verifyNoMoreInteractions(a, b, c);
    }

    @Test
    @SneakyThrows
    void run_CanReturn() {
        var value = new Object();
        doThrow(new Statement.Return(value)).when(b).execute(context);
        assertThat(fixture.run(context)).isEqualTo(value);
        var inOrder = inOrder(a, b, c);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        verifyNoMoreInteractions(a, b, c);
    }

    @Test
    @SneakyThrows
    void run_WrapsOtherExceptions(SeededRng random) {
        when(a.toString()).thenReturn(random.nextString());
        when(b.toString()).thenReturn(random.nextString());
        when(c.toString()).thenReturn(random.nextString());
        var root = random.nextUUID();
        when(context.getRootObject()).thenReturn(new TypedValue(root));
        var e = new RuntimeException(random.nextString());
        doThrow(e).when(b).execute(context);
        assertThatThrownBy(() -> fixture.run(context))
                .hasMessage("Failed executing `%s` for %s in `%s`.", b, root, fixture)
                .hasCause(e);
        var inOrder = inOrder(a, b, c);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        verifyNoMoreInteractions(a, b, c);
    }

    @Test
    void testToString(SeededRng random) {
        when(a.toString()).thenReturn(random.nextString());
        when(b.toString()).thenReturn(random.nextString());
        when(c.toString()).thenReturn(random.nextString());
        assertThat(fixture).hasToString("%s; %s; %s;", a, b, c);
    }

}
