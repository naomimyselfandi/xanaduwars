package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ScriptImplTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private Statement body;

    @InjectMocks
    private ScriptImpl fixture;

    @Test
    void execute(SeededRng random) {
        var foo = random.nextString();
        var bar = random.not(foo);
        var baz = random.not(foo, bar);
        when(body.execute(eq(runtime), any())).then(invocation -> {
            assertThat(invocation.<EvaluationContext>getArgument(1))
                    .isInstanceOfSatisfying(ScriptRootContext.class, context -> {
                        assertThat(context.parent).isEqualTo(runtime);
                        assertThat(context.variables).containsOnly(Map.entry(foo, bar));
                    });
            return baz;
        });
        assertThat(fixture.execute(runtime, Map.of(foo, bar))).isEqualTo(baz);
    }

    @Test
    void execute_WhenAReturnTypeIsGiven_ThenConverts(SeededRng random) {
        var foo = random.nextString();
        var bar = random.not(foo);
        var baz = random.nextInt();
        when(body.execute(eq(runtime), any())).then(invocation -> {
            assertThat(invocation.<EvaluationContext>getArgument(1))
                    .isInstanceOfSatisfying(ScriptRootContext.class, context -> {
                        assertThat(context.parent).isEqualTo(runtime);
                        assertThat(context.variables).containsOnly(Map.entry(foo, bar));
                    });
            return baz;
        });
        assertThat(fixture.execute(runtime, Map.of(foo, bar), TypeDescriptor.valueOf(String.class)))
                .isEqualTo(String.valueOf(baz));
    }

    @Test
    void executeNonNull(SeededRng random) {
        var foo = random.nextString();
        var bar = random.not(foo);
        var baz = random.nextInt();
        when(body.execute(eq(runtime), any())).then(invocation -> {
            assertThat(invocation.<EvaluationContext>getArgument(1))
                    .isInstanceOfSatisfying(ScriptRootContext.class, context -> {
                        assertThat(context.parent).isEqualTo(runtime);
                        assertThat(context.variables).containsOnly(Map.entry(foo, bar));
                    });
            return baz;
        });
        assertThat(fixture.<String>executeNotNull(runtime, Map.of(foo, bar))).isEqualTo(String.valueOf(baz));
    }

    @Test
    void executeAsLibrary(SeededRng random) {
        var foo = random.nextString();
        var bar = random.not(foo);
        var baz = random.not(foo, bar);
        var bat = "_" + random.not(foo, bar, baz);
        when(body.execute(eq(runtime), any())).then(invocation -> {
            assertThat(invocation.<EvaluationContext>getArgument(1))
                    .isInstanceOfSatisfying(ScriptRootContext.class, context -> {
                        assertThat(context.parent).isEqualTo(runtime);
                        assertThat(context.variables).isEmpty();
                        context.variables.put(foo, bar);
                        context.variables.put(baz, null);
                        context.variables.put(bat, new Object());
                    });
            return null;
        });
        assertThat(fixture.executeAsLibrary(runtime)).isEqualTo(new LibraryImpl(Map.of(foo, bar)));
    }

}
