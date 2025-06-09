package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;

record ConstantBeanResolver(GlobalRuleSource globals, BeanResolver delegate) implements BeanResolver {

    @Override
    public Object resolve(EvaluationContext context, String beanName) throws AccessException {
        // Can't use orElseGet() here because of checked exceptions.
        var global = globals.constants().filter(it -> it.toString().equals(beanName)).findFirst();
        return global.isPresent() ? global.get() : delegate.resolve(context, beanName);
    }

}
