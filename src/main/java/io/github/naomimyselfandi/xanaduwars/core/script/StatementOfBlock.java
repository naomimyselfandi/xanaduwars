package io.github.naomimyselfandi.xanaduwars.core.script;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
final class StatementOfBlock implements Statement {

    /// A fake variable used to hold a jump target. This is not a valid variable
    /// name in scripts, so it cannot conflict.
    static final String TARGET_VARIABLE = "#<target>#";

    /// A fake variable used to hold a call result. This is not a valid variable
    /// name in scripts, so it cannot conflict.
    static final String RESULT_VARIABLE = "#<result>#";

    private final List<Statement> statements;
    private final Map<String, Integer> labels;
    private final int end;

    StatementOfBlock(List<StatementOrLabel> code) {
        var statements = new ArrayList<Statement>();
        var labels = new HashMap<String, Integer>();
        for (var it : code) {
            var _ = switch (it) {
                case Label(var name) -> labels.put(name, statements.size());
                case Statement statement -> statements.add(statement);
            };
        }
        this.statements = List.copyOf(statements);
        this.labels = Map.copyOf(labels);
        this.end = this.statements.size();
    }

    @Override
    public @Nullable Object execute(ScriptRuntime runtime, EvaluationContext context) {
        labels.forEach(context::setVariable);
        var iap = 0;
        while (iap < end) {
            statements.get(iap).execute(runtime, context);
            if (context.lookupVariable(TARGET_VARIABLE) instanceof Integer jump) {
                iap = jump;
                context.setVariable(TARGET_VARIABLE, null);
            } else {
                iap++;
            }
        }
        var result = context.lookupVariable(RESULT_VARIABLE);
        return result == Undefined.UNDEFINED ? null : result;
    }

}
