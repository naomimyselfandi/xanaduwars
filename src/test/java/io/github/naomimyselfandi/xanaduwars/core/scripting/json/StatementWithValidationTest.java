package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.checkerframework.checker.nullness.qual.Nullable;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithValidationTest {

    @Mock
    private EvaluationContext context;

    @Mock
    private Expression condition, message;

    private StatementWithValidation fixture;

    @BeforeEach
    void setup() {
        fixture = new StatementWithValidation(condition, message);
    }

    @Test
    void execute_WhenTheConditionHolds_ThenProceeds() {
        when(condition.getValue(context, Boolean.class)).thenReturn(true);
        assertThat(fixture.execute(context, Result.class)).isEqualTo(Statement.PROCEED);
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(booleans = false)
    void execute_WhenTheConditionDoesNotHold_ThenReturnsAFailedResult(@Nullable Boolean nullOrFalse, SeededRng random) {
        var fail = random.<Result.Fail>get();
        when(condition.getValue(context, Boolean.class)).thenReturn(nullOrFalse);
        when(message.getValue(context, Result.class)).thenReturn(fail);
        assertThat(fixture.execute(context, Result.class)).isEqualTo(fail);
    }

    @Test
    void testToString(SeededRng random) {
        var c = random.nextString();
        var m = random.nextString();
        when(condition.getExpressionString()).thenReturn(c);
        when(message.getExpressionString()).thenReturn(m);
        assertThat(fixture).hasToString("validate %s :: %s", c, m);
    }

}
