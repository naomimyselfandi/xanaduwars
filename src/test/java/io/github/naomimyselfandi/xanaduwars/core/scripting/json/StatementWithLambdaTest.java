package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypeLocator;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithLambdaTest {

    private interface Helper {}

    @Mock
    private TypeLocator typeLocator;

    @Mock
    private EvaluationContext context;

    private String name, signature;

    private Script body;

    private List<String> parameters;

    private StatementWithLambda fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = random.get();
        name = fixture.name();
        signature = fixture.signature();
        body = fixture.body();
        parameters = fixture.parameters();
    }

    @Test
    void execute() {
        when(context.getTypeLocator()).thenReturn(typeLocator);
        doReturn(Helper.class).when(typeLocator).findType(signature);
        assertThat(fixture.execute(context, Object.class)).isEqualTo(Statement.PROCEED);
        verify(context).setVariable(eq(name), any(Helper.class));
    }

    @Test
    void testToString() {
        var p = String.join(", ", parameters);
        assertThat(fixture).hasToString("""
                lambda #%s:%s(%s):
                  %s""", name, signature, p, body);
    }

}
