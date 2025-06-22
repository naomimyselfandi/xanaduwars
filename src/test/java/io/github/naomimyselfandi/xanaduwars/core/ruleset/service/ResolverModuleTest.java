package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.TileType;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.UnitType;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ResolverModuleTest {

    private String structureTypeName, unitTypeName, tileType0Name, tileType1Name;

    @Mock
    private StructureType structureType;

    @Mock
    private TileType tileType0, tileType1;

    @Mock
    private UnitType unitType;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(SeededRng random) {
        structureTypeName = random.get();
        unitTypeName = random.get();
        tileType0Name = random.get();
        tileType1Name = random.get();
        objectMapper = new ObjectMapper().registerModule(new ResolverModule(Map.of(
                structureTypeName, structureType,
                unitTypeName, unitType,
                tileType0Name, tileType0,
                tileType1Name, tileType1
        )));
    }

    @Test
    void resolve() throws JsonProcessingException {
        var json = "\"%s\"".formatted(unitTypeName);
        assertThat(objectMapper.readValue(json, UnitType.class)).isEqualTo(unitType);
    }

    @Test
    void resolve_CanUseSupertype() throws JsonProcessingException {
        var json = "\"%s\"".formatted(unitTypeName);
        assertThat(objectMapper.readValue(json, Action.class)).isEqualTo(unitType);
    }

    @Test
    void resolve_WhenTheConstantDoesNotExist_ThenThrows(SeededRng random) {
        var name = random.<String>get();
        var json = "\"%s\"".formatted(name);
        assertThatThrownBy(() -> objectMapper.readValue(json, UnitType.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("Unknown UnitType '%s'.", name);
    }

    @Test
    void resolve_WhenTheConstantIsTneWrongType_ThenThrows() {
        var json = "\"%s\"".formatted(structureTypeName);
        assertThatThrownBy(() -> objectMapper.readValue(json, UnitType.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("'%s' is not a UnitType.", structureTypeName);
    }

    @Test
    void resolve_Map() throws JsonProcessingException {
        var json = """
                {
                  "%s": "%s",
                  "%s": "%s"
                }
                """.formatted(unitTypeName, tileType0Name, structureTypeName, tileType1Name);
        assertThat(objectMapper.readValue(json, new TypeReference<Map<Action, TileType>>() {})).isEqualTo(Map.of(
                unitType, tileType0,
                structureType, tileType1
        ));
    }

}
