package io.github.naomimyselfandi.xanaduwars.core.scripting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ScriptDeserializerTest {

    // See also ScriptParserTest.

    @MethodSource
    @ParameterizedTest
    void deserialize(String json, Script expected) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        assertThat(objectMapper.readValue(json, Script.class)).isEqualTo(expected);
    }

    private static Stream<Arguments> deserialize() {
        return Stream.of(
                arguments("[]", Script.NULL),
                arguments("null", Script.NULL),
                arguments("42", new ScriptImpl(List.of(new StatementWithReturnValue(new Expr("42"))))),
                arguments("4.2", new ScriptImpl(List.of(new StatementWithReturnValue(new Expr("4.2"))))),
                arguments("true", new ScriptImpl(List.of(new StatementWithReturnValue(new Expr("true"))))),
                arguments("false", new ScriptImpl(List.of(new StatementWithReturnValue(new Expr("false"))))),
                arguments("\"fn()\"", new ScriptImpl(List.of(new StatementWithExpression(new Expr("fn()"))))),
                arguments("[\"foo()\", \"bar()\"]", new ScriptImpl(List.of(
                        new StatementWithExpression(new Expr("foo()")),
                        new StatementWithExpression(new Expr("bar()"))
                )))
        );
    }

}
