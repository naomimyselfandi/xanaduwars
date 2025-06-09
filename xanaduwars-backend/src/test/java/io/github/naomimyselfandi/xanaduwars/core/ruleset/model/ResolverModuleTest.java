package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ResolverModuleTest {

    @Mock
    private UnitType unitType0, unitType1;

    @Mock
    private TileType tileType0, tileType1;

    private UnitTag unitTag0, unitTag1;

    @Mock
    private Ruleset ruleset;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(SeededRng random) {
        unitTag0 = random.get();
        unitTag1 = random.get();
        when(ruleset.constants()).then(_ -> Stream.of(
                unitType0,
                unitType1,
                tileType0,
                tileType1,
                unitTag0,
                unitTag1
        ));
        objectMapper = new ObjectMapper().registerModule(new ResolverModule(ruleset));
    }

    @Test
    void deserialize() throws JsonProcessingException {
        assertThat(objectMapper.readValue("\"unitType0\"", UnitType.class)).isEqualTo(unitType0);
        assertThat(objectMapper.readValue("\"unitType1\"", UnitType.class)).isEqualTo(unitType1);
        assertThat(objectMapper.readValue("\"tileType0\"", TileType.class)).isEqualTo(tileType0);
        assertThat(objectMapper.readValue("\"tileType1\"", TileType.class)).isEqualTo(tileType1);
        assertThat(objectMapper.readValue("\"%s\"".formatted(unitTag0.label()), UnitTag.class)).isEqualTo(unitTag0);
        assertThat(objectMapper.readValue("\"%s\"".formatted(unitTag1.label()), UnitTag.class)).isEqualTo(unitTag1);
    }

    @Test
    void deserialize_WhenTheObjectDoesNotExist_ThenThrows() {
        assertThatThrownBy(() -> objectMapper.readValue("\"unitType2\"", UnitType.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("Unknown UnitType 'unitType2'.");
    }

    @Test
    void deserialize_WhenTheObjectIsTheWrongType_ThenThrows() {
        assertThatThrownBy(() -> objectMapper.readValue("\"unitType0\"", TileType.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("'unitType0' is not a TileType.");
    }

}
