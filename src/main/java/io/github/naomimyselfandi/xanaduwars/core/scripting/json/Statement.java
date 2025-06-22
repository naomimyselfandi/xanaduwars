package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.xanaduwars.util.ExcludeFromCoverageReport;
import io.github.naomimyselfandi.xanaduwars.util.Ordinal;
import lombok.With;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;

interface Statement {

    @Nullable Object execute(EvaluationContext context, Class<?> type);

    /// A sentinel value indicating that script execution should continue.
    @ExcludeFromCoverageReport
    record Proceed() {}

    /// A sentinel value indicating that script execution should continue.
    Proceed PROCEED = new Proceed();

    /// A sentinel value indicating a `break` statement was executed.
    @ExcludeFromCoverageReport
    record Break(@With int ordinal) implements Ordinal<Break> {}

    /// A sentinel value indicating a `continue` statement was executed.
    @ExcludeFromCoverageReport
    record Continue(@With int ordinal) implements Ordinal<Continue> {}

}
