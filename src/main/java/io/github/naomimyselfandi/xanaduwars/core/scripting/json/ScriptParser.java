package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import lombok.experimental.StandardException;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.RegExp;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
record ScriptParser() implements Function<String, Script> {

    private static final Statement EMPTY_STATEMENT = new StatementWithBlock(List.of());

    private static final Pattern COMMENT = Pattern.compile("//.*|/\\*([^*]|\\*[^/])*\\*/");
    private static final Pattern CONTINUATION = Pattern.compile("_\\h*\\v+");
    private static final Pattern WHITESPACE = Pattern.compile("(\\S)\\s+");

    @StandardException
    static class ParseException extends RuntimeException {}

    @Override
    public Script apply(String input) {
        var lines = COMMENT.matcher(input)
                .replaceAll(" ")
                .transform(CONTINUATION::matcher)
                .replaceAll(" ")
                .lines()
                .filter(Predicate.not(String::isBlank))
                .map(line -> WHITESPACE.matcher(line).replaceAll("$1 "))
                .toList()
                .listIterator();
        return new Parse(lines).parse();
    }

    private record Parse(ListIterator<String> lines) {

        private static final Pattern INDENTATION = Pattern.compile("^ *");

        private static final Pattern BREAK = Pattern.compile("break($| [1-9])");
        private static final Pattern CONTINUE = Pattern.compile("continue($| [1-9])");
        private static final Pattern RETURN = Pattern.compile("return (.*)");
        private static final Pattern THROW = Pattern.compile("throw (.*)");
        private static final Pattern VALIDATE = Pattern.compile("validate (.*)::(.*)");

        private static final Pattern NAME = Pattern.compile("[a-zA-Z]+");

        private static final Pattern IF = Pattern.compile("if (.*) ?:");
        private static final Pattern ELSE = Pattern.compile("\\s*else\\s*:\\s*");
        private static final Pattern FOR = compile("for #(NAME)  in  (.*) :");
        private static final Pattern WHILE = compile("while  (.*) :");
        private static final Pattern LAMBDA = compile("lambda #(NAME) : (NAME) \\( (|#NAME(?:, #NAME)* )\\) :");

        Script parse() {
            var statements = Stream.<Statement>builder();
            while (lines.hasNext()) {
                statements.add(statement(0, 0));
            }
            return new ScriptImpl(toStatement(statements.build().toList()));
        }

        private Statement statement(int indentationLevel, int loops) {
            if (peekAtIndentationLevel() != indentationLevel) {
                return reject("Inconsistent indentation.");
            }
            Matcher matcher;
            var line = lines.next().trim();
            if ((matcher = BREAK.matcher(line)).matches()) {
                return new StatementWithBreak(depth(matcher, loops));
            } else if ((matcher = CONTINUE.matcher(line)).matches()) {
                return new StatementWithContinue(depth(matcher, loops));
            } else if ((matcher = RETURN.matcher(line)).matches()) {
                return new StatementWithReturnValue(new Expr(matcher.group(1)));
            } else if ((matcher = THROW.matcher(line)).matches()) {
                return new StatementWithException(new Expr(matcher.group(1)));
            } else if ((matcher = VALIDATE.matcher(line)).matches()) {
                var condition = new Expr(matcher.group(1).trim());
                var message = new Expr(matcher.group(2).trim());
                return new StatementWithValidation(condition, message);
            } else if ((matcher = IF.matcher(line)).matches()) {
                var condition = new Expr(matcher.group(1));
                var yes = block(indentationLevel, loops);
                var no = elseBlock(indentationLevel, loops);
                return new StatementWithCondition(condition, yes, no);
            } else if ((matcher = FOR.matcher(line)).matches()) {
                var variable = matcher.group(1);
                var source = new Expr(matcher.group(2));
                var body = block(indentationLevel, loops + 1);
                return new StatementWithForLoop(variable, source, body);
            } else if ((matcher = WHILE.matcher(line)).matches()) {
                var condition = new Expr(matcher.group(1));
                var body = block(indentationLevel, loops + 1);
                return new StatementWithWhileLoop(condition, body);
            } else if ((matcher = LAMBDA.matcher(line)).matches()) {
                var name = matcher.group(1);
                var signature = matcher.group(2);
                var parameters = NAME.matcher(matcher.group(3)).results().map(MatchResult::group).toList();
                var body = new ScriptImpl(block(indentationLevel, 0));
                return new StatementWithLambda(name, signature, parameters, body);
            } else {
                return new StatementWithExpression(new Expr(line));
            }
        }

        private Statement block(int outerLevel, int loops) {
            var innerLevel = peekAtIndentationLevel();
            if (innerLevel <= outerLevel) {
                return reject("Empty block.");
            }
            var result = Stream.<Statement>builder();
            while (peekAtIndentationLevel() == innerLevel) {
                result.add(statement(innerLevel, loops));
            }
            return toStatement(result.build().toList());
        }

        private Statement elseBlock(int outerLevel, int loops) {
            if (peekAtIndentationLevel() == outerLevel && ELSE.matcher(peek()).matches()) {
                lines.next();
                return block(outerLevel, loops);
            } else {
                return EMPTY_STATEMENT;
            }
        }

        private int peekAtIndentationLevel() {
            var matcher = INDENTATION.matcher(peek());
            var _ = matcher.find();
            return matcher.group().length();
        }

        private <T> T reject(String reason) {
            throw new ParseException("Failed parsing `%s`: %s".formatted(peek().trim(), reason));
        }

        private String peek() {
            if (lines.hasNext()) {
                var result = lines.next();
                lines.previous();
                return result;
            } else {
                return "";
            }
        }

        private int depth(Matcher matcher, int loops) {
            var group = matcher.group(1).trim();
            var result = group.isEmpty() ? 1 : Integer.parseInt(group);
            if (result <= loops) {
                return result;
            } else if (loops == 0) {
                lines.previous();
                return reject("No outer loop.");
            } else {
                lines.previous();
                return reject("Only %d outer loop(s).".formatted(loops));
            }
        }

        private static Pattern compile(@RegExp String regex) {
            return regex
                    .replaceAll(" ", " ?")
                    .replaceAll(" \\? \\?", " ")
                    .replaceAll("NAME", NAME.pattern())
                    .transform(Pattern::compile);
        }

    }

    private static Statement toStatement(List<Statement> statements) {
        return switch (statements.size()) {
            case 0 -> EMPTY_STATEMENT;
            case 1 -> statements.getFirst();
            default -> new StatementWithBlock(statements);
        };
    }

}
