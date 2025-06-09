package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ScriptParserTest {

    private ScriptParser fixture;

    @BeforeEach
    void setup() {
        fixture = new ScriptParser();
    }

    @MethodSource
    @ParameterizedTest
    void apply(String input, Script script) {
        assertThat(fixture.apply(input)).isEqualTo(script);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            break,`break` or `continue` encountered outside of loop.
            continue,`break` or `continue` encountered outside of loop.
            while foo(); break 2; done,`break` or `continue` encountered outside of loop.
            while foo(); continue 2; done,`break` or `continue` encountered outside of loop.
            while foo(); bar(),Script ended inside loop.
            for i : foo(); bar(),Script ended inside loop.
            if foo(); bar(),Script ended inside `if` block.
            if foo(); bar(); else; baz(),Script ended inside `else` block.
            while foo(); bar(); else; baz(); done,Unexpected `else` marker.
            while foo(); bar(); fi,Unexpected `fi` marker.
            for i : foo(); bar(); fi,Unexpected `fi` marker.
            if foo(); bar(); done,Unexpected `done` marker.
            if foo(); bar(); else; baz(); done,Unexpected `done` marker.
            if foo(); bar(); else; baz(); else; bat(); fi,Encountered multiple `else` markers.
            """)
    void apply_WhenTheInputIsInvalid_ThenThrows(String input, String message) {
        assertThatThrownBy(() -> fixture.apply(input))
                .isInstanceOf(ScriptParser.ParseException.class)
                .hasMessage(message);
    }

    private static Arguments testCase(String string, Statement... statements) {
        return arguments(string, new ScriptImpl(List.of(statements)));
    }

    private static Stream<Arguments> apply() {
        return Stream.of(
                testCase("foo()", $("foo()")),
                testCase("""
                        foo()
                        bar()
                        """,
                        $("foo()"),
                        $("bar()")),
                testCase("foo(); bar()", $("foo()"), $("bar()")),
                testCase("return foo()", $return("foo()")),
                testCase("throw foo()", $throw("foo()")),
                testCase("validate foo()", $validate("foo()")),
                testCase("""
                        if foo()
                        bar()
                        fi
                        """, $if("foo()", $("bar()"))),
                testCase("if foo(); bar(); fi", $if("foo()", $("bar()"))),
                testCase("if foo(); return bar(); fi", $if("foo()", $return("bar()"))),
                testCase("if foo(); throw bar(); fi", $if("foo()", $throw("bar()"))),
                testCase("if foo(); validate bar(); fi", $if("foo()", $validate("bar()"))),
                testCase("if #a; return #b; else; return #c; fi",
                        $if("#a",
                        List.of($return("#b")),
                        List.of($return("#c"))
                )),
                testCase("while foo(); bar(); done", $while("foo()", $("bar()"))),
                testCase("""
                        while true
                        if foo(); bar(); else; break; fi
                        done
                        """,
                        $while("true",
                                $if("foo()", List.of($("bar()")), List.of($break()))
                        )),
                testCase("""
                        while foo()
                        if bar(); continue; fi
                        baz()
                        done
                        """,
                        $while("foo()",
                                $if("bar()", $continue()),
                                $("baz()")
                        )),
                testCase(
                        "for i : foo(); i.hp(100); done",
                        $for("i", "foo()", $("i.hp(100)"))
                ),
                testCase("""
                        #total = 0.0
                        #count = 0
                        for unit : subject.units()
                          #total = #total + unit.hp()
                          #count++
                        done
                        if #count == 0
                          return T(Double).NaN
                        else
                          return #total / #count
                        fi
                        """,
                        $("#total = 0.0"),
                        $("#count = 0"),
                        $for("unit", "subject.units()",
                                $("#total = #total + unit.hp()"),
                                $("#count++")
                        ),
                        $if("#count == 0",
                                List.of($return("T(Double).NaN")),
                                List.of($return("#total / #count"))
                        )
                )
        );
    }

    private static Statement $(String string) {
        return new StatementWithExpression(new Expr(string));
    }

    private static Statement $break() {
        return new StatementWithBreak(1);
    }

    private static Statement $continue() {
        return new StatementWithContinue(1);
    }

    private static Statement $if(String condition, List<Statement> yes, List<Statement> no) {
        return new StatementWithCondition(new Expr(condition), yes, no);
    }

    private static Statement $if(String condition, Statement... yes) {
        return new StatementWithCondition(new Expr(condition), List.of(yes), List.of());
    }

    private static Statement $while(String condition, Statement... body) {
        return new StatementWithWhileLoop(new Expr(condition), List.of(body));
    }

    private static Statement $for(String variable, String expression, Statement... body) {
        return new StatementWithForLoop(variable, new Expr(expression), List.of(body));
    }

    private static Statement $return(String value) {
        return new StatementWithReturnValue(new Expr(value));
    }

    private static Statement $throw(String value) {
        return new StatementWithException(new Expr(value));
    }

    private static Statement $validate(String value) {
        return new StatementWithValidation(new Expr(value));
    }

}
