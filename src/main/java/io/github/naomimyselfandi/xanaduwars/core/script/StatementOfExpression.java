package io.github.naomimyselfandi.xanaduwars.core.script;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@EqualsAndHashCode
final class StatementOfExpression implements Statement {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    @EqualsAndHashCode.Exclude
    private final ThreadLocal<Expression> expression;

    private final String code;

    StatementOfExpression(String code) {
        this.code = code;
        this.expression = ThreadLocal.withInitial(() -> PARSER.parseExpression(code));
    }

    @Override
    public @Nullable Object execute(ScriptRuntime runtime, EvaluationContext context) {
        var expr = expression.get();
        try {
            return expr.getValue(context);
        } catch (RuntimeException e) {
            var message = "Failed evaluating %s.".formatted(expr.getExpressionString());
            throw new EvaluationException(message, e);
        }
    }

}
