package io.github.naomimyselfandi.xanaduwars.core.common;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.validation.Validation;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class TargetSpecTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        var module = new SimpleModule().addKeyDeserializer(TargetFilter.class, new KeyDeserializer() {
            @Override
            public Object deserializeKey(String key, DeserializationContext context) {
                return Stream
                        .<TargetFilter>concat(Arrays.stream(Iff.values()), Arrays.stream(Kind.values()))
                        .filter(it -> it.toString().equals(key))
                        .findFirst()
                        .orElseGet(Assertions::fail);
            }
        });
        objectMapper = new ObjectMapper().registerModule(module);
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource(textBlock = """
            {}|false
            {"Structure": true}|false
            {"Unit": true}|false
            {"Own": true}|false
            {"Own": false, "Ally": true}|false
            {"Ally": true}|false
            {"Enemy": true}|false
            {"Neutral": true}|false
            {"Structure": true, "Own": true}|true
            {"Structure": true, "Ally": true}|true
            {"Structure": true, "Enemy": true}|true
            {"Structure": true, "Own": true}|true
            {"Structure": true, "Ally": true}|true
            {"Structure": true, "Enemy": true}|true
            {"Tile": true}|true
            {"Tile": true, "Unit": true, "Own": true}|true
            {"Tile": true, "Unit": true, "Own": false, "Ally": true}|true
            {"Tile": true, "Unit": true, "Ally": true}|true
            {"Tile": true, "Unit": true, "Enemy": true}|true
            {"minRange": 0, "maxRange": 0, "Unit": true, "Enemy": true}|true
            {"minRange": 1, "maxRange": 1, "Tile": true}|true
            {"minRange": 1, "maxRange": 2, "Structure": true, "Ally": true}|true
            {"minRange": 2, "maxRange": 1, "Tile": true}|false
            {"path": true}|true
            {"path": true, "Tile": true}|false
            {"path": true, "maxRange": 2}|false
            """, delimiter = '|')
    void isValid(String json, boolean expected) {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var spec = objectMapper.readValue(json, TargetSpec.class);
            var violations = factory.getValidator().validate(spec);
            assertThat(violations.isEmpty()).isEqualTo(expected);
        }
    }

    @Test
    void defaultRange() {
        var spec = TargetSpec.builder().build();
        assertThat(spec).returns(0, TargetSpec::minRange).returns(1, TargetSpec::maxRange);
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource(textBlock = """
            {}|false|false|false|false
            {"Tile": true}|false|false|false|true
            {"Ally": true}|true|true|false|false
            {"Ally": true, "Own": false}|false|true|false|false
            {"Ally": true, "Own": false, "Neutral": true}|false|true|false|true
            {"Ally": true, "Own": false, "Neutral": false}|false|true|false|false
            """, delimiter = '|')
    void builder_Iff(String json, boolean own, boolean ally, boolean enemy, boolean neutral) {
        var filters = objectMapper.readValue(json, TargetSpec.class).filters();
        assertThat(filters.getOrDefault(Iff.OWN, false)).isEqualTo(own);
        assertThat(filters.getOrDefault(Iff.ALLY, false)).isEqualTo(ally);
        assertThat(filters.getOrDefault(Iff.ENEMY, false)).isEqualTo(enemy);
        assertThat(filters.getOrDefault(Iff.NEUTRAL, false)).isEqualTo(neutral);
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource(textBlock = """
            {}|false|false|false
            {"Structure": true}|true|false|false
            {"Tile": true}|false|true|false
            {"Unit": true}|false|false|true
            """, delimiter = '|')
    void builder_Kind(String json, boolean structure, boolean tile, boolean unit) {
        var filters = objectMapper.readValue(json, TargetSpec.class).filters();
        assertThat(filters.getOrDefault(Kind.STRUCTURE, false)).isEqualTo(structure);
        assertThat(filters.getOrDefault(Kind.TILE, false)).isEqualTo(tile);
        assertThat(filters.getOrDefault(Kind.UNIT, false)).isEqualTo(unit);
    }

}
