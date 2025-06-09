package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.Nullable;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithValidationTest {

    @Mock
    private EvaluationContext context;

    @Mock
    private Expression condition;

    @InjectMocks
    private StatementWithValidation fixture;

    @Test
    void execute_WhenTheConditionHolds_ThenDoesNothing() {
        when(condition.getValue(context, Boolean.class)).thenReturn(true);
        assertThatCode(() -> fixture.execute(context)).doesNotThrowAnyException();
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(booleans = false)
    void execute_WhenTheConditionDoesNotHold_ThenThrows(@Nullable Boolean nullOrFalse) {
        when(condition.getValue(context, Boolean.class)).thenReturn(nullOrFalse);
        assertThatThrownBy(() -> fixture.execute(context)).isEqualTo(new Statement.Return(false));
    }

    @Test
    void testToString(SeededRng random) {
        var expressionString = random.nextString();
        when(condition.getExpressionString()).thenReturn(expressionString);
        assertThat(fixture).hasToString("validate %s", expressionString);
    }

}
