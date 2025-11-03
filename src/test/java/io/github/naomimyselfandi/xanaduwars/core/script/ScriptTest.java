package io.github.naomimyselfandi.xanaduwars.core.script;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScriptTest {

    @Mock
    private ScriptRuntime runtime;

    @Test
    void zeroConstant() {
        assertThat(Script.ZERO.execute(runtime, Map.of())).isEqualTo(0);
        verifyNoInteractions(runtime);
    }

    @Test
    void trueConstant() {
        assertThat(Script.TRUE.execute(runtime, Map.of())).isEqualTo(true);
        verifyNoInteractions(runtime);
    }

    @Test
    void execute() {
        var list = new ArrayList<>();
        when(runtime.lookup(any())).then(i -> i.getArgument(0).equals("Output") ? list : null);
        var script = Script.of("""
                def fibFactory(start):
                  #cache = new HashMap()
                  def fib(n):
                    (n <= 1) && return(start)
                    #result = cache[n]
                    (result != null) && return(result)
                    result = fib(n - 2) + fib(n - 1)
                    cache[n] = result
                    return(result)
                  end
                  return(fib)
                end
                
                def emit(count, fib):
                  #i = 0
                  #result = 0
                  label loop:
                  (i >= count) && return(result)
                  result = fib(i)
                  Output.add(result)
                  i++
                  goto(loop)
                end
 
                return(emit(x, fibFactory(1)))
                """.lines().toList());
        assertThat(script.execute(runtime, Map.of("x", 6))).isEqualTo(8);
        assertThat(script.execute(runtime, Map.of("x", 7))).isEqualTo(13);
        assertThat(list).containsExactly(1, 1, 2, 3, 5, 8, 1, 1, 2, 3, 5, 8, 13);
    }

    @Test
    void execute_CanRetrieveTheRuntime() {
        assertThat(Script.of("$").execute(runtime, Map.of())).isEqualTo(runtime);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    @ValueSource(doubles = {0.1, 0.2})
    @ValueSource(booleans = {true, false})
    void of(Object constant) {
        var script = switch (constant) {
            case Boolean b -> Script.of(b);
            case Double d -> Script.of(d);
            case Integer i -> Script.of(i);
            default -> throw new IllegalArgumentException();
        };
        assertThat(script.execute(runtime, Map.of())).isEqualTo(constant);
        verifyNoInteractions(runtime);
    }

}
