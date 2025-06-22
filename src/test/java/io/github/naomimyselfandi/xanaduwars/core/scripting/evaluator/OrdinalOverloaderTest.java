package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class OrdinalOverloaderTest {

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final EvaluationContext CONTEXT;

    static  {
        var context = new StandardEvaluationContext();
        context.setTypeLocator(_ -> TestOrdinal.class);
        CONTEXT = context;
    }

    @Mock
    private OperatorOverloader delegate;

    @InjectMocks
    private OrdinalOverloader fixture;

    @ParameterizedTest
    @CsvSource(textBlock = """
            ADD,T(TestOrdinal).of(40),T(TestOrdinal).of(2),false
            SUBTRACT,T(TestOrdinal).of(50),T(TestOrdinal).of(8),false
            MULTIPLY,T(TestOrdinal).of(6),T(TestOrdinal).of(7),false
            DIVIDE,T(TestOrdinal).of(84),T(TestOrdinal).of(2),false
            MODULUS,T(TestOrdinal).of(42),T(TestOrdinal).of(10),false
            POWER,T(TestOrdinal).of(3),T(TestOrdinal).of(2),false
            ADD,T(TestOrdinal).of(40),2,false
            SUBTRACT,T(TestOrdinal).of(50),8,false
            MULTIPLY,T(TestOrdinal).of(6),7,false
            DIVIDE,T(TestOrdinal).of(84),2,false
            MODULUS,T(TestOrdinal).of(42),10,false
            POWER,T(TestOrdinal).of(3),2,false
            ADD,40,T(TestOrdinal).of(2),false
            SUBTRACT,50,T(TestOrdinal).of(8),false
            MULTIPLY,6,T(TestOrdinal).of(7),false
            DIVIDE,84,T(TestOrdinal).of(2),false
            MODULUS,42,T(TestOrdinal).of(10),false
            POWER,3,T(TestOrdinal).of(2),false
            ADD,40,2,false
            ADD,'foo','bar',true
            ADD,40,'bar',true
            ADD,'foo',2,true
            ADD,T(TestOrdinal).of(40),'bar',true
            ADD,'foo',T(TestOrdinal).of(2),true
            """, quoteCharacter = '"')
    void overridesOperation(Operation operation, String lhs, String rhs, boolean shouldCheckDelegate) {
        var l = evaluate(lhs);
        var r = evaluate(rhs);
        if (shouldCheckDelegate) {
            for (var value : List.of(true, false)) {
                when(delegate.overridesOperation(operation, l, r)).thenReturn(value);
                assertThat(fixture.overridesOperation(operation, l, r)).isEqualTo(value);
            }
        } else {
            assertThat(fixture.overridesOperation(operation, l, r)).isTrue();
        }
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            ADD,T(TestOrdinal).of(40),T(TestOrdinal).of(2),T(TestOrdinal).of(42)
            SUBTRACT,T(TestOrdinal).of(50),T(TestOrdinal).of(8),T(TestOrdinal).of(42)
            MULTIPLY,T(TestOrdinal).of(6),T(TestOrdinal).of(7),T(TestOrdinal).of(42)
            DIVIDE,T(TestOrdinal).of(84),T(TestOrdinal).of(2),T(TestOrdinal).of(42)
            MODULUS,T(TestOrdinal).of(42),T(TestOrdinal).of(10),T(TestOrdinal).of(2)
            POWER,T(TestOrdinal).of(3),T(TestOrdinal).of(2),9.0
            ADD,T(TestOrdinal).of(40),2,T(TestOrdinal).of(42)
            SUBTRACT,T(TestOrdinal).of(50),8,T(TestOrdinal).of(42)
            MULTIPLY,T(TestOrdinal).of(6),7,T(TestOrdinal).of(42)
            DIVIDE,T(TestOrdinal).of(84),2,T(TestOrdinal).of(42)
            MODULUS,T(TestOrdinal).of(42),10,T(TestOrdinal).of(2)
            POWER,T(TestOrdinal).of(3),2,9.0
            ADD,40,T(TestOrdinal).of(2),T(TestOrdinal).of(42)
            SUBTRACT,50,T(TestOrdinal).of(8),T(TestOrdinal).of(42)
            MULTIPLY,6,T(TestOrdinal).of(7),T(TestOrdinal).of(42)
            DIVIDE,84,T(TestOrdinal).of(2),T(TestOrdinal).of(42)
            MODULUS,42,T(TestOrdinal).of(10),T(TestOrdinal).of(2)
            POWER,3,T(TestOrdinal).of(2),9.0
            ADD,'foo','bar',
            ADD,40,'bar',
            ADD,'foo',2,
            ADD,T(TestOrdinal).of(40),'bar',
            ADD,'foo',T(TestOrdinal).of(2),
            """, quoteCharacter = '"')
    void operate(Operation operation, String lhs, String rhs, @Nullable String expected) {
        var l = evaluate(lhs);
        var r = evaluate(rhs);
        if (expected == null) {
            var result = new Object();
            when(delegate.operate(operation, l, r)).thenReturn(result);
            assertThat(fixture.operate(operation, l, r)).isEqualTo(result);
        } else {
            var e = evaluate(expected);
            assertThat(fixture.operate(operation, l, r)).isEqualTo(e);
        }
    }

    private @Nullable Object evaluate(String string) {
        return PARSER.parseExpression(string).getValue(CONTEXT);
    }

}
