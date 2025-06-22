package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.core.scripting.GlobalRuleSource;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;

record ConstantBeanResolver(GlobalRuleSource globals) implements BeanResolver {

    @Override
    public Object resolve(EvaluationContext context, String beanName) throws AccessException {
        var global = globals.getScriptConstants().get(beanName);
        if (global != null) {
            return global;
        } else {
            throw new AccessException("Couldn't find a declaration named '@%s'.".formatted(beanName));
        }
    }

}
