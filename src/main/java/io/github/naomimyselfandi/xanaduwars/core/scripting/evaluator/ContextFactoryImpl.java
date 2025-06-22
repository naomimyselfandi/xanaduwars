package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import org.jetbrains.annotations.VisibleForTesting;
import org.springframework.expression.*;
import org.springframework.expression.spel.support.*;
import org.springframework.stereotype.Service;

@Service
class ContextFactoryImpl implements ContextFactory {

    @VisibleForTesting
    static final TypeConverter BASE_CONVERTER = new StandardTypeConverter();

    @VisibleForTesting
    static final TypeComparator BASE_COMPARATOR = new StandardTypeComparator();

    @VisibleForTesting
    static final OperatorOverloader BASE_OVERLOADER = new StandardOperatorOverloader();

    @VisibleForTesting
    static final MethodResolver BASE_RESOLVER = new ReflectiveMethodResolver();

    private static final TypeLocator LOCATOR;
    private static final TypeConverter CONVERTER;
    private static final TypeComparator COMPARATOR;
    private static final OperatorOverloader OVERLOADER;
    private static final MethodResolver RESOLVER;
    private static final PropertyAccessor ACCESSOR;

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
        CONVERTER = new ResultCreator(new OrdinalCreator(new OrdinalConverter(BASE_CONVERTER)));
        COMPARATOR = new OrdinalComparator(BASE_COMPARATOR);
        OVERLOADER = new OrdinalOverloader(BASE_OVERLOADER);
        RESOLVER = new IterableMethodResolver(BASE_RESOLVER);
        ACCESSOR = new NullMethodPropertyAccessor(RESOLVER);
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
        result.setTypeComparator(COMPARATOR);
        result.setOperatorOverloader(OVERLOADER);
        result.setTypeLocator(LOCATOR);
        result.getMethodResolvers().add(RESOLVER);   // addMethodResolver() puts it at the beginning
        result.getPropertyAccessors().add(ACCESSOR); // ditto addPropertyAccessor()
        return result;
    }

}
