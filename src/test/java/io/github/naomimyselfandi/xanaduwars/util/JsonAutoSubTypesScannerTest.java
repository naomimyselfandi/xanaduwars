package io.github.naomimyselfandi.xanaduwars.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class JsonAutoSubTypesScannerTest {

    @Mock
    private Jackson2ObjectMapperBuilder builder;

    private final JsonAutoSubTypesScanner fixture = new JsonAutoSubTypesScanner();

    @Test
    void customize() throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        when(builder.postConfigurer(any())).then(it -> {
            it.<Consumer<ObjectMapper>>getArgument(0).accept(objectMapper);
            return null;
        });
        fixture.customize(builder);
        assertThat(objectMapper.readValue("{\"llama\": \"Foo\"}", Helper.class)).isEqualTo(new Foo());
        assertThat(objectMapper.readValue("{\"llama\": \"Baz\"}", Helper.class)).isEqualTo(new Bar());
    }

}
