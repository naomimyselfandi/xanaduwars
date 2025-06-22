package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class OrdinalComparatorTest {

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final EvaluationContext CONTEXT;

    static  {
        var context = new StandardEvaluationContext();
        context.setTypeLocator(_ -> TestOrdinal.class);
        CONTEXT = context;
    }

    @Mock
    private TypeComparator delegate;

    @InjectMocks
    private OrdinalComparator fixture;

    @ParameterizedTest
    @CsvSource(textBlock = """
            T(TestOrdinal).of(40),2,false
            40,T(TestOrdinal).of(2),false
            T(TestOrdinal).of(40),T(TestOrdinal).of(2),false
            40,2,false
            'foo','bar',true
            'foo',2,true
            'foo',T(TestOrdinal).of(2),true
            T(TestOrdinal).of(40),'bar',true
            """, quoteCharacter = '"')
    void canCompare(String lhs, String rhs, boolean shouldCheckDelegate) {
        var l = evaluate(lhs);
        var r = evaluate(rhs);
        if (shouldCheckDelegate) {
            for (var value : List.of(true, false)) {
                when(delegate.canCompare(l, r)).thenReturn(value);
                assertThat(fixture.canCompare(l, r)).isEqualTo(value);
            }
        } else {
            assertThat(fixture.canCompare(l, r)).isTrue();
        }
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            42,43,-1
            42,42,0
            42,41,1
            T(TestOrdinal).of(42),43,-1
            T(TestOrdinal).of(42),42,0
            T(TestOrdinal).of(42),41,1
            42,T(TestOrdinal).of(43),-1
            42,T(TestOrdinal).of(42),0
            42,T(TestOrdinal).of(41),1
            T(TestOrdinal).of(42),T(TestOrdinal).of(43),-1
            T(TestOrdinal).of(42),T(TestOrdinal).of(42),0
            T(TestOrdinal).of(42),T(TestOrdinal).of(41),1
            'foo','bar',
            null,T(TestOrdinal).of(42),
            T(TestOrdinal).of(42),null,
            """, quoteCharacter = '"')
    void compare(String lhs, String rhs, @Nullable Integer expected, SeededRng random) {
        var l = evaluate(lhs);
        var r = evaluate(rhs);
        if (expected == null) {
            var result = random.nextInt();
            when(delegate.compare(l, r)).thenReturn(result);
            assertThat(fixture.compare(l, r)).isEqualTo(result);
        } else {
            assertThat(fixture.compare(l, r)).isEqualTo((int) Math.signum(expected));
        }
    }

    private @Nullable Object evaluate(String string) {
        return PARSER.parseExpression(string).getValue(CONTEXT);
    }

}
