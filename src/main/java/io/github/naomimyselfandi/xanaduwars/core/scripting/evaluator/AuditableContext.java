package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import org.jetbrains.annotations.Nullable;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

class AuditableContext extends StandardEvaluationContext {

    private final Set<String> variableNames = new LinkedHashSet<>();

    @Override
    public void setVariable(@Nullable String name, @Nullable Object value) {
        super.setVariable(name, value);
        if (name != null) {
            if (value == null) {
                variableNames.remove(name);
            } else {
                variableNames.add(name);
            }
        }
    }

    @Override
    public String toString() {
        return variableNames
                .stream()
                .map(variable -> ", %s=%s".formatted(variable, lookupVariable(variable)))
                .collect(Collectors.joining("", "{root=%s".formatted(getRootObject().getValue()), "}"));
    }

}
