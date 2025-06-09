package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ConstantBeanResolverTest {

    @Mock
    private EvaluationContext evaluationContext;

    @Mock
    private BeanResolver delegate;

    @Mock
    private GlobalRuleSource source;

    @Mock
    private ScriptConstant foo, bar;

    @InjectMocks
    private ConstantBeanResolver fixture;

    @BeforeEach
    void setup() {
        when(source.constants()).then(_ -> Stream.of(foo, bar));
    }

    @Test
    void resolve() throws AccessException {
        assertThat(fixture.resolve(evaluationContext, "foo")).isEqualTo(foo);
        assertThat(fixture.resolve(evaluationContext, "bar")).isEqualTo(bar);
        verifyNoInteractions(delegate);
    }

    @Test
    void resolve_WhenNoBeanCanBeFound_Delegates() throws AccessException {
        var baz = new Object();
        when(delegate.resolve(evaluationContext, "baz")).thenReturn(baz);
        assertThat(fixture.resolve(evaluationContext, "baz")).isEqualTo(baz);
    }

}
