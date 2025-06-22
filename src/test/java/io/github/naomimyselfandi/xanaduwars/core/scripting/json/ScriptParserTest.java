package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.expression.Expression;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SuppressWarnings("SameParameterValue")
class ScriptParserTest {

    @TestCase("")
    private static final Statement EMPTY_STATEMENT = $block();

    @TestCase({
            "foo()",
            "foo() // bar"
    })
    private static final Statement FOO = $eval("foo()");

    @TestCase("bar()")
    private static final Statement BAR = $eval("bar()");

    @TestCase("baz()")
    private static final Statement BAZ = $eval("baz()");

    @TestCase({
            """
            foo()
            bar()
            baz()
            """,
            """
            foo() // a
            bar() // b
            baz() // c
            """,
            """
            foo() /* a */
            bar() /* b */
            baz() /* c */
            """,
    })
    private static final Statement FOO_BAR_BAZ = $block(FOO, BAR, BAZ);

    @TestCase({
            "return foo()",
            "return /* bar */ foo()",
            """
            return _
            foo()
            """
    })
    private static final Statement RETURN_FOO = $return("foo()");

    @TestCase({
            "throw bar()",
            "throw /* foo */ bar()",
            """
            throw _
            bar()
            """
    })
    private static final Statement THROW_BAR = $throw("bar()");

    @TestCase({
            "validate baz() :: 'bat'",
            "validate /* foo */ baz() :: 'bat'",
            """
            validate _
            baz() :: 'bat'
            """
    })
    private static final Statement VALIDATE_BAZ = $validate("baz()", "'bat'");

    @TestCase({
            """
            while a():
              foo()
              bar()
              baz()
            """
    })
    private static final Statement WHILE = $while("a()", FOO_BAR_BAZ);

    @TestCase({
            """
            for #a in b:
              foo()
              bar()
              baz()
            """
    })
    private static final Statement FOR = $for("a", $("b"), FOO_BAR_BAZ);

    @TestCase({
            """
            if a:
              foo()
              bar()
              baz()
            """
    })
    private static final Statement IF = $if($("a"), FOO_BAR_BAZ, EMPTY_STATEMENT);

    @TestCase({
            """
            if a:
              foo()
              bar()
              baz()
            else:
              foo()
              bar()
              baz()
            """
    })
    private static final Statement IF_ELSE = $if($("a"), FOO_BAR_BAZ, FOO_BAR_BAZ);

    @TestCase({
            """
            if a:
              foo()
              bar()
              baz()
            else:
              foo()
              bar()
              baz()
            for #a in b:
              foo()
              bar()
              baz()
            """
    })
    private static final Statement IF_ELSE_FOR = $block(IF_ELSE, FOR);

    @TestCase({
            """
            if a:
              foo()
              bar()
              baz()
            if a:
              foo()
              bar()
              baz()
            """
    })
    private static final Statement MULTI_IF = $block(IF, IF);

    @TestCase({
            """
            if a:
              b()
              if c:
                d()
            else:
              e()
            """
    })
    private static final Statement MULTI_ELSE = $if(
            $("a"),
            $block(
                    $eval("b()"),
                    $if($("c"), $eval("d()"), EMPTY_STATEMENT)
            ),
            $eval("e()")
    );

    @TestCase({
            """
            if a:
              b()
              for #c in d:
                if e(#c):
                  f()
                else:
                  g()
              h()
            else:
              i()
            j()
            """
    })
    private static final Statement NESTING = $block(
            $if("a",
                    $block(
                            $eval("b()"),
                            $for("c",
                                    $("d"),
                                    $if($("e(#c)"), $eval("f()"), $eval("g()"))
                            ),
                            $eval("h()")
                    ),
                    $eval("i()")
            ),
            $eval("j()")
    );

    @TestCase({
            """
            while foo():
              if bar():
                break
              baz()
            """,
            """
            while foo():
              if bar():
                break 1
              baz()
            """,
    })
    private static final Statement BREAK_WHILE = $while(
            $("foo()"),
            $block($if($("bar()"), $break(1), EMPTY_STATEMENT), $eval("baz()"))
    );

    @TestCase({
            """
            while foo():
              if bar():
                continue
              baz()
            """,
            """
            while foo():
              if bar():
                continue 1
              baz()
            """,
    })
    private static final Statement CONTINUE_WHILE = $while(
            $("foo()"),
            $block(
                    $if($("bar()"), $continue(1), EMPTY_STATEMENT),
                    $eval("baz()")
            )
    );

    @TestCase({
            """
            for #a in b:
              if c(#a):
                break
              d(#a)
            """,
            """
            for #a in b:
              if c(#a):
                break 1
              d(#a)
            """,
    })
    private static final Statement BREAK_FOR = $for(
            "a",
            $("b"),
            $block(
                    $if($("c(#a)"), $break(1), EMPTY_STATEMENT),
                    $eval("d(#a)")
            )
    );

    @TestCase({
            """
            for #a in b:
              if c(#a):
                continue
              d(#a)
            """,
            """
            for #a in b:
              if c(#a):
                continue 1
              d(#a)
            """,
    })
    private static final Statement CONTINUE_FOR = $for(
            "a",
            $("b"),
            $block(
                    $if($("c(#a)"), $continue(1), EMPTY_STATEMENT),
                    $eval("d(#a)")
            )
    );

    @TestCase({
            """
            lambda #fallback:Supplier():
              return -1
            lambda #even:Predicate(#i):
              return (#i % 1) == 0
            lambda #sum:BinaryOperator(#a, #b):
              return #a + #b
            return foo.filter(#even).reduce(#sum).orElseGet(fallback)
            """, """
            lambda#fallback:Supplier():
              return -1
            lambda#even:Predicate(#i):
              return (#i % 1) == 0
            lambda#sum:BinaryOperator(#a,#b):
              return #a + #b
            return foo.filter(#even).reduce(#sum).orElseGet(fallback)
            """, """
            lambda #fallback : Supplier ( ) :
              return -1
            lambda #even : Predicate ( #i ):
              return (#i % 1) == 0
            lambda #sum : BinaryOperator ( #a, #b ) :
              return #a + #b
            return foo.filter(#even).reduce(#sum).orElseGet(fallback)
            """
    })
    private static final Statement LAMBDAS = $block(
            $lambda("fallback", "Supplier", $return("-1")),
            $lambda("even", "Predicate", $return("(#i % 1) == 0"), "i"),
            $lambda("sum", "BinaryOperator", $return("#a + #b"), "a", "b"),
            $return("foo.filter(#even).reduce(#sum).orElseGet(fallback)")
    );

    @TestCase("break")
    private static final ScriptParser.ParseException BREAK_OUTSIDE_LOOP = new ScriptParser.ParseException(
            "Failed parsing `break`: No outer loop."
    );

    @TestCase("continue")
    private static final ScriptParser.ParseException CONTINUE_OUTSIDE_LOOP = new ScriptParser.ParseException(
            "Failed parsing `continue`: No outer loop."
    );

    @TestCase("""
           while a():
             break 2
           """)
    private static final ScriptParser.ParseException BREAK_TOO_FAR = new ScriptParser.ParseException(
            "Failed parsing `break 2`: Only 1 outer loop(s)."
    );

    @TestCase("""
           while a():
             continue 2
           """)
    private static final ScriptParser.ParseException CONTINUE_TOO_FAR = new ScriptParser.ParseException(
            "Failed parsing `continue 2`: Only 1 outer loop(s)."
    );

    @TestCase("""
            if foo():
            bar()
            """)
    private static final ScriptParser.ParseException MISSING_INDENTATION = new ScriptParser.ParseException(
            "Failed parsing `bar()`: Empty block."
    );

    @TestCase(" foo()")
    private static final ScriptParser.ParseException EARLY_INDENTATION = new ScriptParser.ParseException(
            "Failed parsing `foo()`: Inconsistent indentation."
    );

    @TestCase("""
            if foo():
              bar()
             baz()
            """)
    private static final ScriptParser.ParseException INCONSISTENT_INDENTATION = new ScriptParser.ParseException(
            "Failed parsing `baz()`: Inconsistent indentation."
    );

    private ScriptParser fixture;

    @BeforeEach
    void setup() {
        fixture = new ScriptParser();
    }

    @MethodSource
    @ParameterizedTest
    void parse_Valid(String input, Statement statement) {
        assertThat(fixture.apply(input)).isEqualTo(new ScriptImpl(statement));
    }

    @MethodSource
    @ParameterizedTest
    void parse_Invalid(String input, ScriptParser.ParseException parseException) {
        assertThatThrownBy(() -> fixture.apply(input))
                .isInstanceOf(ScriptParser.ParseException.class)
                .hasMessage(parseException.getMessage());
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface TestCase {
        String[] value();
    }

    private static Stream<Arguments> parse_Valid() {
        return Arrays
                .stream(ScriptParserTest.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(TestCase.class))
                .filter(field -> field.getType() != ScriptParser.ParseException.class)
                .flatMap(field -> {
                    var value = get(field);
                    var annotation = field.getAnnotation(TestCase.class);
                    assert annotation != null;
                    return Arrays
                            .stream(annotation.value())
                            .map(it -> arguments(it, value));
                });
    }

    private static Stream<Arguments> parse_Invalid() {
        return Arrays
                .stream(ScriptParserTest.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(TestCase.class))
                .filter(field -> field.getType() == ScriptParser.ParseException.class)
                .flatMap(field -> {
                    var value = get(field);
                    var annotation = field.getAnnotation(TestCase.class);
                    assert annotation != null;
                    return Arrays
                            .stream(annotation.value())
                            .map(it -> arguments(it, value));
                });
    }

    @SneakyThrows
    private static Object get(Field field) {
        field.setAccessible(true);
        return field.get(null);
    }

    private static Expression $(String string) {
        return new Expr(string);
    }

    private static Statement $block(Statement... statements) {
        return new StatementWithBlock(List.of(statements));
    }

    private static Statement $eval(String string) {
        return new StatementWithExpression($(string));
    }

    private static Statement $return(String string) {
        return new StatementWithReturnValue($(string));
    }

    private static Statement $throw(String string) {
        return new StatementWithException($(string));
    }

    private static Statement $validate(String condition, String message) {
        return new StatementWithValidation($(condition), $(message));
    }

    private static Statement $if(String condition, Statement yes, Statement no) {
        return $if($(condition), yes, no);
    }

    private static Statement $if(Expression condition, Statement yes, Statement no) {
        return new StatementWithCondition(condition, yes, no);
    }

    private static Statement $while(String condition, Statement body) {
        return $while($(condition), body);
    }

    private static Statement $while(Expression condition, Statement body) {
        return new StatementWithWhileLoop(condition, body);
    }

    private static Statement $for(String variable, Expression source, Statement body) {
        return new StatementWithForLoop(variable, source, body);
    }

    private static Statement $break(int depth) {
        return new StatementWithBreak(depth);
    }

    private static Statement $continue(int depth) {
        return new StatementWithContinue(depth);
    }

    private static Statement $lambda(String name, String signature, Statement body, String... parameters) {
        return new StatementWithLambda(name, signature, List.of(parameters), new ScriptImpl(body));
    }

}
