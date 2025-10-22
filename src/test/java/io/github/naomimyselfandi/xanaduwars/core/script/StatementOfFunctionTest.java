package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementOfFunctionTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private EvaluationContext context;

    private String name, foo, bar;

    @Mock
    private Statement instruction;

    private StatementOfFunction fixture;

    @BeforeEach
    void setup(SeededRng random) {
        name = random.nextString();
        foo = random.nextString();
        bar = random.nextString();
        fixture = new StatementOfFunction(name, List.of(foo, bar), instruction);
    }

    @Test
    void execute() {
        assertThat(fixture.execute(runtime, context)).isNull();
        verify(context).setVariable(name, new FunctionImpl(runtime, context, name, List.of(foo, bar), instruction));
    }

}
