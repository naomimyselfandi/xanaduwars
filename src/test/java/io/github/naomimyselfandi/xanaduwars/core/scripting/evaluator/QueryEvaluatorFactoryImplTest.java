package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QueryEvaluatorFactoryImplTest {

    @Mock
    private GlobalRuleSource globalRuleSource;

    @Mock
    private ContextFactory contextFactory;

    @Mock
    private ScriptSelector scriptSelector;

    @InjectMocks
    private QueryEvaluatorFactoryImpl fixture;

    @Test
    void create() {
        var expected = new QueryEvaluatorImpl(globalRuleSource, contextFactory, scriptSelector);
        assertThat(fixture.create(globalRuleSource)).isEqualTo(expected);
    }

}
