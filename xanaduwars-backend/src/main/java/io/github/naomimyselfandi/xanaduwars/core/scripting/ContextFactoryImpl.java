package io.github.naomimyselfandi.xanaduwars.core.scripting;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ContextFactoryImpl implements ContextFactory {

    @Qualifier("beanFactoryResolver")
    private final BeanResolver beanResolver;

    private static final String IMPORTS = """
            io.github.naomimyselfandi.xanaduwars.core.gameplay.state
            io.github.naomimyselfandi.xanaduwars.core.gameplay.queries
            io.github.naomimyselfandi.xanaduwars.core.ruleset.model
            """.trim();

    @Override
    public EvaluationContext create(GlobalRuleSource globals, Query<?> query) {
        var typeLocator = new StandardTypeLocator();
        IMPORTS.lines().forEach(typeLocator::registerImport);
        var result = new StandardEvaluationContext();
        result.setTypeLocator(typeLocator);
        result.setRootObject(query);
        result.setBeanResolver(new ConstantBeanResolver(globals, beanResolver));
        return result;
    }

}
