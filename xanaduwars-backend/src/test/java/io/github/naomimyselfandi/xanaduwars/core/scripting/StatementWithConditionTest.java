package io.github.naomimyselfandi.xanaduwars.core.scripting;

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

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithConditionTest {

    @Mock
    private EvaluationContext context;

    @Mock
    private Expression condition;

    @Mock
    private Statement a, b, c, x, y, z;

    private StatementWithCondition fixture;

    @BeforeEach
    void setup() {
        fixture = new StatementWithCondition(condition, List.of(a, b, c), List.of(x, y, z));
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
        var statements = conditionHolds ? List.of(a, b, c) : List.of(x, y, z);
        fixture.execute(context);
        var inOrder = inOrder(statements.toArray());
        for (var statement : statements) {
            inOrder.verify(statement).execute(context);
        }
        verifyNoMoreInteractions(a, b, c, x, y, z);
    }

    @Test
    void testToString(SeededRng random) {
        when(condition.getExpressionString()).thenReturn(random.nextString());
        when(a.toString()).thenReturn(random.nextString());
        when(b.toString()).thenReturn(random.nextString());
        when(c.toString()).thenReturn(random.nextString());
        when(x.toString()).thenReturn(random.nextString());
        when(y.toString()).thenReturn(random.nextString());
        when(z.toString()).thenReturn(random.nextString());
        var template = "if %s; %s; %s; %s; else; %s; %s; %s; fi";
        assertThat(fixture).hasToString(template, condition.getExpressionString(), a, b, c, x, y, z);
    }

}
