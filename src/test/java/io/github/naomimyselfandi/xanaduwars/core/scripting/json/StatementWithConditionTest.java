package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithConditionTest {

    private interface Helper {}

    @Mock
    private Helper helper;

    @Mock
    private EvaluationContext context;

    @Mock
    private Expression condition;

    @Mock
    private Statement yes, no;

    private StatementWithCondition fixture;

    @BeforeEach
    void setup() {
        fixture = new StatementWithCondition(condition, yes, no);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true
            false,false
            ,false
            """)
    @SneakyThrows
    void execute(@Nullable Boolean conditionResult, boolean conditionHolds) {
        when(condition.getValue(context, Boolean.class)).thenReturn(conditionResult);
        var statement = conditionHolds ? yes : no;
        when(statement.execute(context, Helper.class)).thenReturn(helper);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(helper);
        verify(statement).execute(context, Helper.class);
        verifyNoMoreInteractions(yes, no);
    }

    @Test
    void testToString(SeededRng random) {
        when(condition.getExpressionString()).thenReturn(random.nextString());
        when(yes.toString()).thenReturn(random.nextString());
        when(no.toString()).thenReturn(random.nextString());
        assertThat(fixture).hasToString("""
                if %s:
                  %s
                else:
                  %s""", condition.getExpressionString(), yes, no);
    }

}
