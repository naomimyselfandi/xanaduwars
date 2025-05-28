package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Tag;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.TagSet;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BiFilterDeserializerTest {

    private final Name name = new Name("Sonja");

    private final Tag tag = new Tag("Air");

    private record Helper(BiFilter<Unit, Unit> unitFilter, BiFilter<Tile, Tile> tileFilter) {}

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void deserialize(boolean hasRuleset) throws JsonProcessingException {
        if (hasRuleset) injectRuleset();
        @Language("json") var json = """
                {
                  "unitFilter": "!tag.Air",
                  "tileFilter": "unit.owner.name.Sonja"
                }
                """;
        assertThat(objectMapper.readValue(json, Helper.class)).isEqualTo(new Helper(
                new BiFilterNot<>(new BiFilterOfTag<>(tag)),
                new BiFilterUsingUnit<>(new BiFilterUsingOwner<>(new BiFilterOfName<>(name)))
        ));
    }

    @Test
    void deserialize_WhenARulesetIsAvailable_ThenUsesIt() {
        injectRuleset();
        @Language("json") var json = """
                {
                  "unitFilter": "tag.Air",
                  "tileFilter": "tag.Air"
                }
                """;
        assertThatThrownBy(() -> objectMapper.readValue(json, Helper.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("Unknown Tile tag 'Air'.");
    }

    @Test
    void deserialize_Null() throws JsonProcessingException {
        assertThat(objectMapper.readValue("null", BiFilter.class)).isEqualTo(new BiFilterYes<>());
    }

    @Test
    void deserialize_EmbeddedNull() throws JsonProcessingException {
        record Helper(BiFilter<Object, Object> filter) {}
        assertThat(objectMapper.readValue("{}", Helper.class)).isEqualTo(new Helper(new BiFilterYes<>()));
    }

    @Test
    void deserialize_BizarreHack() throws JsonProcessingException {
        assertThat(objectMapper.readValue("{}", BiFilter.class)).isEqualTo(new BiFilterYes<>());
    }

    private void injectRuleset() {
        var unitType = mock(UnitType.class);
        when(unitType.tags()).thenReturn(TagSet.of(tag));
        var commander = mock(Commander.class);
        when(commander.name()).thenReturn(name);
        var ruleset = mock(Ruleset.class);
        when(ruleset.unitTypes()).thenReturn(List.of(unitType));
        when(ruleset.commanders()).thenReturn(List.of(commander));
        var injectable = new InjectableValues.Std().addValue("ruleset", ruleset);
        objectMapper.setInjectableValues(injectable);
    }

}
