package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Specification;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class NameSerializerTest {

    @Mock
    private Specification declaration1, declaration2;

    private String name1, name2;

    @BeforeEach
    void setup(SeededRng random) {
        name1 = random.get();
        name2 = random.not(name1);
        when(declaration1.getName()).thenReturn(name1);
        when(declaration2.getName()).thenReturn(name2);
    }

    @Test
    void serialize() throws JsonProcessingException {
        record Helper(@JsonSerialize(contentUsing = NameSerializer.class) List<Specification> declarations) {}
        record Helper2(List<String> declarations) {}
        var helper = new Helper(new ArrayList<>() {{ add(declaration1); add(null); add(declaration2); }});
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(helper);
        assertThat(objectMapper.readValue(json, Helper2.class).declarations)
                .containsExactly(name1, null, name2);
    }

}
