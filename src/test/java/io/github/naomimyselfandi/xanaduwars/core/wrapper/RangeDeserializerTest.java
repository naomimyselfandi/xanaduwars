package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class RangeDeserializerTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            0 1 {"min":0,"max":1}
            1 2 {"min":1,"max":2}
            0 1 "0-1"
            1 2 "1-2"
            1 1 null
            """, delimiter = ' ')
    void test(int min, int max, String json) throws JsonProcessingException {
        assertThat(new ObjectMapper().readValue(json, Range.class)).isEqualTo(new Range(min, max));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            "1"
            "1-"
            "-1"
            "-1-2"
            """)
    void test_WhenAStringLiteralHasTheWrongSyntax_ThenThrows(String json) {
        assertThatThrownBy(() -> new ObjectMapper().readValue(json, Range.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("Malformed range '%s", json.replaceAll("\"", ""));
    }

}
