package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static org.assertj.core.api.Assertions.*;

class ToStringSerializerTest {

    @Test
    void serialize() throws JsonProcessingException {
        record Helper(@JsonSerialize(using = ToStringSerializer.class) int foo) {}
        assertThat(new ObjectMapper().writeValueAsString(new Helper(42))).isEqualTo("{\"foo\":\"42\"}");
    }

}
