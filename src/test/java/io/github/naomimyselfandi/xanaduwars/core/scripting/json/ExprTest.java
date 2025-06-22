package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.Expression;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ExprTest {

    @Mock
    private Expression expression;

    @InjectMocks
    private Expr fixture;

    @Test
    void testEquals() {
        when(expression.getExpressionString()).thenReturn("foo");
        var foo = new Expr("foo");
        var bar = new Expr("bar");
        assertThat(fixture).isEqualTo(foo).isNotEqualTo(bar).isNotEqualTo(bar.expression());
    }

    @Test
    void testHashCode(SeededRng random) {
        var string = random.nextString();
        when(expression.getExpressionString()).thenReturn(string);
        assertThat(fixture).hasSameHashCodeAs(string);
    }

    @Test
    void testToString(SeededRng random) {
        var string = random.nextString();
        when(expression.getExpressionString()).thenReturn(string);
        assertThat(fixture).hasToString(string);
    }

    @Test
    void getValue() {
        var value = new Object();
        when(expression.getValue()).thenReturn(value);
        assertThat(fixture.getValue()).isEqualTo(value);
    }

}
