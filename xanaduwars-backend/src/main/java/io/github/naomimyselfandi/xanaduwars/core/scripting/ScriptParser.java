package io.github.naomimyselfandi.xanaduwars.core.scripting;

import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

record ScriptParser() implements Function<String, Script> {

    private static final Pattern COMMENT = Pattern.compile("//.*|/\\*([^*]|\\*[^/])*\\*/");
    private static final Pattern CONTINUATION = Pattern.compile("\\\\\\h*\\v+");
    private static final Pattern WHITESPACE = Pattern.compile("\\h+");
    private static final Pattern SEPARATOR = Pattern.compile(";");

    @StandardException
    static class ParseException extends RuntimeException {}

    @Override
    public Script apply(String input) {
        var parse = new Parse();
        COMMENT.matcher(input)
                .replaceAll(" ")
                .transform(CONTINUATION::matcher)
                .replaceAll(" ")
                .transform(WHITESPACE::matcher)
                .replaceAll(" ")
                .lines()
                .flatMap(SEPARATOR::splitAsStream)
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .forEach(parse);
        return parse.get();
    }

    private static class Parse implements Consumer<String>, Supplier<Script> {

        private static final Pattern BREAK = Pattern.compile("break($| [1-9])");
        private static final Pattern CONTINUE = Pattern.compile("continue($| [1-9])");
        private static final Pattern RETURN = Pattern.compile("return (.*)");
        private static final Pattern THROW = Pattern.compile("throw (.*)");
        private static final Pattern VALIDATE = Pattern.compile("validate (.*)");

        private static final Pattern IF = Pattern.compile("if (.*)");
        private static final Pattern ELSE = Pattern.compile("else");
        private static final Pattern END_IF = Pattern.compile("fi");

        private static final Pattern FOR = Pattern.compile("for ([a-zA-Z]+) ?: ?(.*)");
        private static final Pattern WHILE = Pattern.compile("while (.*)");
        private static final Pattern END_LOOP = Pattern.compile("done");

        private final List<Statement> statements = new ArrayList<>();
        private State state = new RootState(statements::add);

        @Override
        public void accept(String string) {
            Matcher matcher;
            if ((matcher = BREAK.matcher(string)).matches()) {
                var depth = depth(matcher);
                state.requireLoop(depth);
                state = state.accept(new StatementWithBreak(depth));
            } else if ((matcher = CONTINUE.matcher(string)).matches()) {
                var depth = depth(matcher);
                state.requireLoop(depth);
                state = state.accept(new StatementWithContinue(depth));
            } else if ((matcher = RETURN.matcher(string)).matches()) {
                state = state.accept(new StatementWithReturnValue(new Expr(matcher.group(1))));
            } else if ((matcher = THROW.matcher(string)).matches()) {
                state = state.accept(new StatementWithException(new Expr(matcher.group(1))));
            } else if ((matcher = VALIDATE.matcher(string)).matches()) {
                state = state.accept(new StatementWithValidation(new Expr(matcher.group(1))));
            } else if ((matcher = IF.matcher(string)).matches()) {
                state = new IfState(state, new Expr(matcher.group(1).trim()));
            } else if (ELSE.matcher(string).matches()) {
                state = state.acceptElse();
            } else if (END_IF.matcher(string).matches()) {
                state = state.acceptEndIf();
            } else if ((matcher = FOR.matcher(string)).matches()) {
                state = new LoopState(state, matcher.group(1), new Expr(matcher.group(2)));
            } else if ((matcher = WHILE.matcher(string)).matches()) {
                state = new LoopState(state, null, new Expr(matcher.group(1)));
            } else if (END_LOOP.matcher(string).matches()) {
                state = state.acceptEndLoop();
            } else {
                state = state.accept(new StatementWithExpression(new Expr(string)));
            }
        }

        @Override
        public Script get() {
            state.acceptEof();
            return new ScriptImpl(statements);
        }

        private int depth(Matcher matcher) {
            var group = matcher.group(1).trim();
            return group.isEmpty() ? 1 : Integer.parseInt(group);
        }

    }

    private interface State {

        State accept(Statement statement);

        void acceptEof();

        void requireLoop(int depth);

        default State acceptElse() {
            throw new ParseException("Unexpected `else` marker.");
        }

        default State acceptEndIf() {
            throw new ParseException("Unexpected `fi` marker.");
        }

        default State acceptEndLoop() {
            throw new ParseException("Unexpected `done` marker.");
        }

    }

    private record RootState(Consumer<Statement> consumer) implements State {

        @Override
        public State accept(Statement statement) {
            consumer.accept(statement);
            return this;
        }

        @Override
        public void acceptEof() {}

        @Override
        public void requireLoop(int depth) {
            if (depth > 0) throw new ParseException("`break` or `continue` encountered outside of loop.");
        }

    }

    @RequiredArgsConstructor
    private static class IfState implements State {

        private final State parent;
        private final Expression condition;
        private final List<Statement> yes = new ArrayList<>();
        private final List<Statement> no = new ArrayList<>();
        private boolean foundElse;

        @Override
        public State accept(Statement statement) {
            (foundElse ? no : yes).add(statement);
            return this;
        }

        @Override
        public void acceptEof() {
            throw new ParseException("Script ended inside `%s` block.".formatted(foundElse ? "else" : "if"));
        }

        @Override
        public void requireLoop(int depth) {
            parent.requireLoop(depth);
        }

        @Override
        public State acceptElse() {
            if (foundElse) {
                throw new ParseException("Encountered multiple `else` markers.");
            } else {
                foundElse = true;
                return this;
            }
        }

        @Override
        public State acceptEndIf() {
            return parent.accept(new StatementWithCondition(condition, yes, no));
        }

    }

    @RequiredArgsConstructor
    private static class LoopState implements State {

        private final State parent;
        private final @Nullable String variable;
        private final Expression expression;
        private final List<Statement> body = new ArrayList<>();

        @Override
        public State accept(Statement statement) {
            body.add(statement);
            return this;
        }

        @Override
        public void acceptEof() {
            throw new ParseException("Script ended inside loop.");
        }

        @Override
        public void requireLoop(int depth) {
            parent.requireLoop(depth - 1);
        }

        @Override
        public State acceptEndLoop() {
            return parent.accept(variable == null
                    ? new StatementWithWhileLoop(expression, body)
                    : new StatementWithForLoop(variable, expression, body));
        }

    }

}
