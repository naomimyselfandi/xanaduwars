package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;

non-sealed interface Statement extends StatementOrLabel {

    @Nullable Object execute(ScriptRuntime runtime, EvaluationContext context);

}
