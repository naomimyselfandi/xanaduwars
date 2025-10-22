package io.github.naomimyselfandi.xanaduwars.core.script;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ScriptParserTest {

    @MethodSource
    @ParameterizedTest
    void parse(String code, Script expected) {
        assertThat(ScriptParser.parse(code.lines().toList())).isEqualTo(expected);
    }

    @MethodSource
    @ParameterizedTest
    void parse_AppliesNormalization(String code, String norm) {
        assertThat(ScriptParser.parse(code.lines().toList())).isEqualTo(ScriptParser.parse(norm.lines().toList()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "foo?[bar]",
            "foo![bar]",
            "foo^[bar]",
            "foo$[bar]",
            "foo.[bar]"
    })
    void parse_DoesNotApplyInappropriateNormalizations(String code) {
        var expected = new ScriptImpl(new StatementOfBlock(List.of(new StatementOfExpression(code))));
        assertThat(ScriptParser.parse(List.of(code))).isEqualTo(expected);
    }

    @Test
    void parse_WhenTheInputEndsInAFunctionBody_ThenThrows() {
        var code = """
                def foo(bar, baz):
                  frobnicate(bar + baz)
                foo(40, 2)
                """.lines().toList();
        assertThatThrownBy(() -> ScriptParser.parse(code))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Code ended in function 'foo'.");
    }

    private static Stream<Arguments> parse() {
        return Stream.of(
                arguments("""
                        def sum(lhs, rhs):
                          return(lhs + rhs)
                        end
                        
                        return(ints.stream.reduce(0, sum))
                        """, new ScriptImpl(new StatementOfBlock(List.of(
                        new StatementOfFunction(
                                "sum",
                                List.of("lhs", "rhs"),
                                new StatementOfBlock(List.of(
                                        new StatementOfExpression("return(lhs + rhs)")
                                ))),
                        new StatementOfExpression("return(ints.stream.reduce(0, sum))")
                )))),
                arguments("""
                        // the janky recursive Fibonacci implementation from CS 101
                        def fib(x):
                          (x <= 1) && goto(base) // contrived use of jumps for testing
                          return(fib(x - 2) + fib(x - 1))
                          label base:
                          return(1)
                        end
                        
                        return(fib(x))
                        """, new ScriptImpl(new StatementOfBlock(List.of(
                        new StatementOfFunction(
                                "fib",
                                List.of("x"),
                                new StatementOfBlock(List.of(
                                        new StatementOfExpression("(x <= 1) && goto(base)"),
                                        new StatementOfExpression("return(fib(x - 2) + fib(x - 1))"),
                                        new Label("base"),
                                        new StatementOfExpression("return(1)")
                                ))
                        ),
                        new StatementOfExpression("return(fib(x))")
                ))))
        );
    }

    private static Stream<Arguments> parse_AppliesNormalization() {
        return Stream.of(
                arguments("foo // this is a comment", "foo"),
                arguments("foo\n+\nbar", "foo+bar"),
                arguments("foo\n-\nbar", "foo-bar"),
                arguments("foo\n*\nbar", "foo*bar"),
                arguments("foo\n/\nbar", "foo/bar"),
                arguments("foo\n%\nbar", "foo%bar"),
                arguments("foo\n&&\nbar", "foo&&bar"),
                arguments("foo\n||\nbar", "foo||bar"),
                arguments("foo\n.\nbar", "foo.bar"),
                arguments("frobnicate(\n  foo,\n  bar\n)", "frobnicate(foo,bar)"),
                arguments("{\n  foo,  \nbar\n}", "{foo,bar}"),
                arguments("foo[bar]", "foo[#bar]")
        );
    }

}
