package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ConstantBeanResolverTest {

    @Mock
    private EvaluationContext evaluationContext;

    @Mock
    private GlobalRuleSource source;

    private String fooString, barString;

    @Mock
    private Object foo, bar;

    @InjectMocks
    private ConstantBeanResolver fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fooString = random.get();
        barString = random.get();
        when(source.getScriptConstants()).thenReturn(Map.of(fooString, foo, barString, bar));
    }

    @Test
    void resolve() throws AccessException {
        assertThat(fixture.resolve(evaluationContext, fooString)).isEqualTo(foo);
        assertThat(fixture.resolve(evaluationContext, barString)).isEqualTo(bar);
    }

    @Test
    void resolve_WhenNoBeanCanBeFound_ThenThrows(SeededRng random) {
        var bazString = random.nextString();
        assertThatThrownBy(() -> fixture.resolve(evaluationContext, bazString))
                .isInstanceOf(AccessException.class)
                .hasMessage("Couldn't find a declaration named '@%s'.", bazString);
    }

}
