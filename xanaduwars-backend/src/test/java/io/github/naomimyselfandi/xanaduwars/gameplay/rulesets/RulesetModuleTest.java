package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RulesetModuleTest {

    @Mock
    private Commander commander0, commander1;

    @Mock
    private SpellType<?> spellType0, spellType1;

    @Mock
    private TileType tileType0, tileType1;

    @Mock
    private UnitType unitType0, unitType1;

    @Mock
    private Ruleset ruleset;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        var commanders = setupTypes(commander0, commander1);
        when(ruleset.commanders()).thenReturn(commanders);
        var spellTypes = setupTypes(spellType0, spellType1);
        when(ruleset.spellTypes()).thenReturn(spellTypes);
        var tileTypes = setupTypes(tileType0, tileType1);
        when(ruleset.tileTypes()).thenReturn(tileTypes);
        var unitTypes = setupTypes(unitType0, unitType1);
        when(ruleset.unitTypes()).thenReturn(unitTypes);
        objectMapper = new ObjectMapper().registerModule(new RulesetModule(ruleset));
    }

    @Test
    void test() throws JsonProcessingException {
        record Helper(
                List<NodeType> list,
                Map<TileType, UnitType> map,
                Map<Commander, SpellType<?>> spells
        ) {}
        @Language("json")
        var json = """
                {
                  "list": ["UnitType0", "TileType1", "TileType0", "UnitType1"],
                  "map": {"TileType0": "UnitType0", "TileType1": "UnitType1"},
                  "spells": {
                    "Commander0": "SpellType0",
                    "Commander1": "SpellType1"
                  }
                }
                """;
        var expected = new Helper(
                List.of(unitType0, tileType1, tileType0, unitType1),
                Map.of(tileType0, unitType0, tileType1, unitType1),
                Map.of(commander0, spellType0, commander1, spellType1)
        );
        var actual = objectMapper.readValue(json, Helper.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void test_WhenTheTypeDoesNotExist_ThenThrows() {
        assertThatThrownBy(() -> objectMapper.readValue("\"UnitType42\"", UnitType.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("Unknown UnitType 'UnitType42'.");
    }

    @Test
    void test_WhenTheTypeIsTheWrongKind_ThenThrows() {
        assertThatThrownBy(() -> objectMapper.readValue("\"TileType0\"", UnitType.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("'TileType0' is not a UnitType.");
    }

    @SafeVarargs
    private static <T extends Type> List<T> setupTypes(T... types) {
        for (var type : types) {
            var string = type.toString();
            var name = new Name(string.substring(0, 1).toUpperCase() + string.substring(1));
            when(type.name()).thenReturn(name);
        }
        return List.of(types);
    }

}
