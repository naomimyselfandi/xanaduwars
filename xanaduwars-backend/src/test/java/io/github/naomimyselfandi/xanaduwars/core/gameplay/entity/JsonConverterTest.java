package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class JsonConverterTest {

    private JsonConverter fixture;

    @BeforeEach
    void setup() {
        fixture = new JsonConverter();
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void convertToDatabaseColumn(@Nullable String dbData, @Nullable JsonNode attribute) {
        assertThat(fixture.convertToDatabaseColumn(attribute)).isEqualTo(dbData);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void convertToEntityAttribute(@Nullable String dbData, @Nullable JsonNode attribute) {
        assertThat(fixture.convertToEntityAttribute(dbData)).isEqualTo(attribute);
    }

    @Test
    void convertToEntityAttribute_WhenTheConversionFails_ThenWrapsTheException() {
        assertThatThrownBy(() -> fixture.convertToEntityAttribute("foobar"))
                .hasCauseInstanceOf(IOException.class);
    }

    private static Stream<Arguments> testCases() throws JsonProcessingException {
        @Language("json") var json = """
                {"foo":1,"bar":2}
                """;
        var jsonNode = new ObjectMapper().readTree(json);
        return Stream.of(
                arguments(json.trim(), jsonNode),
                arguments("true", BooleanNode.TRUE),
                arguments("false", BooleanNode.FALSE),
                arguments(null, null)
        );
    }

}
