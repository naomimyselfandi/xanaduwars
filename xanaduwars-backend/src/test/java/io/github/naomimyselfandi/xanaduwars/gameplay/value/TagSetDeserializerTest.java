package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class TagSetDeserializerTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            {"foo": "Abc"}
            {"foo": ["Abc"]}
            """)
    void test(String json) throws IOException {
        record Helper(TagSet foo, TagSet bar) {}
        var expected = new Helper(TagSet.of(new Tag("Abc")), TagSet.EMPTY);
        assertThat(new ObjectMapper().readValue(json, Helper.class)).isEqualTo(expected);
    }

}
