package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.common.Iff;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Hp;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.UnitType;
import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectiveMethodResolver;
import org.springframework.expression.spel.support.StandardOperatorOverloader;
import org.springframework.expression.spel.support.StandardTypeComparator;
import org.springframework.expression.spel.support.StandardTypeConverter;

import java.util.ArrayList;
import java.util.stream.Stream;

import static io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator.ContextFactoryImpl.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContextFactoryImplTest {

    private Object game;

    @Mock
    private GlobalRuleSource globalRuleSource;

    @Mock
    private Query<?> query;

    @InjectMocks
    private ContextFactoryImpl fixture;

    private EvaluationContext context;

    @BeforeEach
    void setup() {
        game = new Object();
        context = fixture.create(globalRuleSource, query, game);
    }

    @Test
    void create_SupportsAuditing() {
        assertThat(context).isInstanceOf(AuditableContext.class);
    }

    @Test
    void create_RegistersTheQuery() {
        assertThat(context.getRootObject()).isEqualTo(new TypedValue(query));
    }

    @Test
    void create_RegistersTheGame() {
        assertThat(context.getPropertyAccessors()).contains(new GameAccessor(new TypedValue(game)));
    }

    @Test
    void create_RegistersTheGlobals() {
        assertThat(context.getBeanResolver()).isEqualTo(new ConstantBeanResolver(globalRuleSource));
    }

    @Test
    void create_RegistersTheTypeConverter() {
        var expected = new ResultCreator(new OrdinalCreator(new OrdinalConverter(BASE_CONVERTER)));
        assertThat(context.getTypeConverter()).isEqualTo(expected);
        assertThat(BASE_CONVERTER).isInstanceOf(StandardTypeConverter.class);
    }

    @Test
    void create_RegistersTheTypeComparator() {
        var expected = new OrdinalComparator(BASE_COMPARATOR);
        assertThat(context.getTypeComparator()).isEqualTo(expected);
        assertThat(BASE_COMPARATOR).isInstanceOf(StandardTypeComparator.class);
    }

    @Test
    void create_RegistersTheOperatorOverloader() {
        var expected = new OrdinalOverloader(BASE_OVERLOADER);
        assertThat(context.getOperatorOverloader()).isEqualTo(expected);
        assertThat(BASE_OVERLOADER).isInstanceOf(StandardOperatorOverloader.class);
    }

    @Test
    void create_RegistersTheMethodResolver() {
        var resolvers = context.getMethodResolvers();
        assertThat(resolvers).last().isEqualTo(new IterableMethodResolver(BASE_RESOLVER));
        assertThat(BASE_RESOLVER).isInstanceOf(ReflectiveMethodResolver.class);
    }

    @Test
    void create_RegistersThePropertyAccessor() {
        var resolvers = context.getPropertyAccessors();
        assertThat(resolvers).last().isEqualTo(new NullMethodPropertyAccessor(context.getMethodResolvers().getLast()));
    }

    @ParameterizedTest
    @ValueSource(classes = {
            Iff.class,
            Unit.class,
            Hp.class,
            UnitType.class,
            ArrayList.class,
            Stream.class
    })
    void create_RegistersTypes(Class<?> type) {
        var expression = new SpelExpressionParser().parseExpression("T(%s)".formatted(type.getSimpleName()));
        assertThat(expression.getValue(context)).isEqualTo(type);
    }

}
