package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ScriptSelectorImplTest {

    @Mock
    private Rule rule0, rule1, rule2, rule3;

    @Mock
    private Script script0, script1, script2, script3, unusedScript;

    @Mock
    private GlobalRuleSource globals;

    @InjectMocks
    private ScriptSelectorImpl fixture;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void select(boolean subjectIsNull) {
        when(rule0.getHandlers()).thenReturn(Map.of(
                new QueryName("Baz"), script1,
                new QueryName("Bar"), unusedScript
        ));
        when(rule1.getHandlers()).thenReturn(Map.of(
                new QueryName("Baz"), script2,
                new QueryName("Bar"), unusedScript
        ));
        when(rule2.getHandlers()).thenReturn(Map.of(new QueryName("Bar"), unusedScript));
        when(rule3.getHandlers()).thenReturn(Map.of());
        when(globals.getRules()).thenReturn(List.of(rule0, rule1, rule2, rule3));
        var query = new BazEvent(subjectIsNull ? null : new Object(), script0, script3);
        assertThat(fixture.select(globals, query)).containsExactly(script0, script1, script2, script3);
    }

    @Test
    void select_WhenTheSubjectIsARuleSource_ThenUsesThoseRules() {
        record Helper(Rule... ruleArray) implements RuleSource {

            @Override
            public List<Rule> getRules() {
                return List.of(ruleArray);
            }


        }
        when(rule0.getHandlers()).thenReturn(Map.of(
                new QueryName("Baz"), script1,
                new QueryName("Bar"), unusedScript
        ));
        when(rule1.getHandlers()).thenReturn(Map.of());
        when(rule2.getHandlers()).thenReturn(Map.of(
                new QueryName("Baz"), script2,
                new QueryName("Bar"), unusedScript
        ));
        when(rule3.getHandlers()).thenReturn(Map.of());
        when(globals.getRules()).thenReturn(List.of(rule0, rule1));
        var query = new BazEvent(new Helper(rule2, rule3), script0, script3);
        assertThat(fixture.select(globals, query)).containsExactly(script0, script1, script2, script3);
    }

}
