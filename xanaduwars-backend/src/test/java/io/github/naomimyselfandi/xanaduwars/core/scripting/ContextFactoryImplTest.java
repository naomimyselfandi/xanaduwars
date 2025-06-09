package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.Event;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContextFactoryImplTest {

    @Mock
    private GlobalRuleSource globalRuleSource;

    @Mock
    private BeanResolver beanResolver;

    @Mock
    private Query<?> query;

    @InjectMocks
    private ContextFactoryImpl fixture;

    private EvaluationContext context;

    @BeforeEach
    void setup() {
        context = fixture.create(globalRuleSource, query);
    }

    @Test
    void create_RegistersTheRootObject() {
        assertThat(context.getRootObject()).isEqualTo(new TypedValue(query));
    }

    @Test
    void create_RegistersTheGlobals() {
        assertThat(context.getBeanResolver()).isEqualTo(new ConstantBeanResolver(globalRuleSource, beanResolver));
    }

    @Test
    void create_RegistersTheGameStateTypes() {
        var expression = new SpelExpressionParser().parseExpression("T(Unit)");
        assertThat(expression.getValue(context)).isEqualTo(Unit.class);
    }

    @Test
    void create_RegistersTheQueryTypes() {
        var expression = new SpelExpressionParser().parseExpression("T(Event)");
        assertThat(expression.getValue(context)).isEqualTo(Event.class);
    }

    @Test
    void create_RegistersTheRulesetTypes() {
        var expression = new SpelExpressionParser().parseExpression("T(UnitType)");
        assertThat(expression.getValue(context)).isEqualTo(UnitType.class);
    }

}
