package io.github.naomimyselfandi.xanaduwars.core.script;

import org.springframework.expression.*;

final class MethodReferenceBeanResolver implements BeanResolver {

    @Override
    public Object resolve(EvaluationContext context, String beanName) {
        return new MethodReference(context, beanName);
    }

}
