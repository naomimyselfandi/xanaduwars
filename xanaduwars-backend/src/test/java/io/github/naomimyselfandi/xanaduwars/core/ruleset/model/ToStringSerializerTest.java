package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class ToStringSerializerTest {

    @Test
    void serialize(SeededRng random) throws JsonProcessingException {
        record Helper(@JsonValue @JsonSerialize(using = ToStringSerializer.class) int value) {}
        var helper = random.<Helper>get();
        assertThat(new ObjectMapper().writeValueAsString(helper)).isEqualTo("\"%d\"", helper.value);
    }

}
