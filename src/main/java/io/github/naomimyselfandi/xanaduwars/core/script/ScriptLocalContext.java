package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;

final class ScriptLocalContext extends ScriptEvaluationContext<EvaluationContext> {

    ScriptLocalContext(EvaluationContext parent) {
        super(parent);
    }

    @Override
    @Nullable Object lookupMissingVariable(String name) {
        return parent.lookupVariable(name);
    }

}
