package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.ScriptingException;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithBlockTest {

    private interface Helper {}

    @Mock
    private Helper helper;

    @Mock
    private EvaluationContext context;

    @Mock
    private Statement foo, bar;

    private StatementWithBlock fixture;

    @BeforeEach
    void setup() {
        fixture = new StatementWithBlock(List.of(foo, bar));
    }

    @Test
    void testToString(SeededRng random) {
        when(foo.toString()).thenReturn(random.nextString());
        when(bar.toString()).thenReturn(random.nextString());
        assertThat(fixture).hasToString("""
                %s
                %s""", foo, bar);
    }

    @Test
    void execute() {
        when(foo.execute(context, Helper.class)).thenReturn(Statement.PROCEED);
        when(bar.execute(context, Helper.class)).thenReturn(Statement.PROCEED);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(Statement.PROCEED);
        var inOrder = inOrder(foo, bar);
        Statement statement1 = inOrder.verify(foo);
        statement1.execute(context, Helper.class);
        Statement statement = inOrder.verify(bar);
        statement.execute(context, Helper.class);
    }

    @Test
    void execute_CanReturn() {
        when(foo.execute(context, Helper.class)).thenReturn(Statement.PROCEED);
        when(bar.execute(context, Helper.class)).thenReturn(helper);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(helper);
        var inOrder = inOrder(foo, bar);
        Statement statement1 = inOrder.verify(foo);
        statement1.execute(context, Helper.class);
        Statement statement = inOrder.verify(bar);
        statement.execute(context, Helper.class);
    }

    @Test
    void execute_WhenAStatementThrows_ThenWrapsTheException(SeededRng random) {
        when(foo.toString()).thenReturn(random.nextString());
        var e = new RuntimeException();
        when(foo.execute(context, Helper.class)).thenThrow(e);
        assertThatThrownBy(() -> fixture.execute(context, Helper.class))
                .isInstanceOf(ScriptingException.class)
                .hasMessage("Failed executing `%s`.", foo)
                .hasCause(e);
    }

    @MethodSource
    @ParameterizedTest
    void execute_WhenANonFinalStatementReturns_ThenSkipsTheRest(@Nullable Object result) {
        when(foo.execute(context, Helper.class)).thenReturn(result);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(result);
        verify(foo).execute(context, Helper.class);
        verifyNoInteractions(bar);
    }

    private static Stream<@Nullable Object> execute_WhenANonFinalStatementReturns_ThenSkipsTheRest() {
        return Stream.of(
                null,
                mock(Helper.class),
                new Statement.Break(1),
                new Statement.Break(2),
                new Statement.Continue(1),
                new Statement.Continue(2)
        );
    }

}
