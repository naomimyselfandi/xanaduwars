package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import lombok.experimental.Delegate;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

record Expr(@Delegate Expression expression) implements Expression {

    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    Expr(String expression) {
        this(EXPRESSION_PARSER.parseExpression(expression));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Expr && toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return expression.getExpressionString();
    }

}
