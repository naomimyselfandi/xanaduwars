package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import org.springframework.expression.*;
import org.springframework.expression.spel.support.*;
import org.springframework.stereotype.Service;

@Service
class ContextFactoryImpl implements ContextFactory {

    private static final TypeLocator LOCATOR;
    private static final TypeConverter CONVERTER;

    static {
        var typeLocator = new StandardTypeLocator();
        """
        io.github.naomimyselfandi.xanaduwars.core.common
        io.github.naomimyselfandi.xanaduwars.core.gamestate
        io.github.naomimyselfandi.xanaduwars.core.gamestate.dto
        io.github.naomimyselfandi.xanaduwars.core.gamestate.queries
        io.github.naomimyselfandi.xanaduwars.core.ruleset
        java.util
        java.util.function
        java.util.stream""".lines().forEach(typeLocator::registerImport);
        LOCATOR = typeLocator;
        CONVERTER = new ResultConverter(new OrdinalCreator(new StandardTypeConverter()));
    }

    @Override
    public EvaluationContext create(GlobalRuleSource globals, Query<?> query, Object game) {
        var result = create(query);
        result.setBeanResolver(new ConstantBeanResolver(globals));
        result.addPropertyAccessor(new GameAccessor(new TypedValue(game)));
        return result;
    }

    private static StandardEvaluationContext create(Query<?> query) {
        var result = new AuditableContext();
        result.setRootObject(query);
        result.setTypeConverter(CONVERTER);
        result.setTypeLocator(LOCATOR);
        result.getMethodResolvers().replaceAll(CustomMethodResolver::new);
        result.getPropertyAccessors().replaceAll(CustomPropertyAccessor::new);
        for (var resolver : result.getMethodResolvers()) {
            // addPropertyAccessor() adds it at the front, but it should be a fallback.
            result.getPropertyAccessors().add(new NullMethodPropertyAccessor(resolver));
        }
        return result;
    }

}
