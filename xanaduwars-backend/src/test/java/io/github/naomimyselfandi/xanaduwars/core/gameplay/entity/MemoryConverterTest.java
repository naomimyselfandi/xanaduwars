package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTypeId;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class MemoryConverterTest {

    private MemoryConverter fixture;

    @BeforeEach
    void setup() {
        fixture = new MemoryConverter();
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void convertToDatabaseColumn(@Nullable String dbData, @Nullable Memory attribute) {
        assertThat(fixture.convertToDatabaseColumn(attribute)).isEqualTo(dbData);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void convertToEntityAttribute(@Nullable String dbData, @Nullable Memory attribute) {
        assertThat(fixture.convertToEntityAttribute(dbData)).isEqualTo(attribute);
    }

    private static Stream<Arguments> testCases() {
        var zero = new PlayerId(0);
        var one = new PlayerId(1);
        var two = new PlayerId(2);
        var typeOne = new StructureTypeId(1);
        var typeTwo = new StructureTypeId(2);
        return Stream.of(
                arguments("", new Memory(Map.of())),
                arguments("1", new Memory(Map.of(zero, typeOne))),
                arguments("2", new Memory(Map.of(zero, typeTwo))),
                arguments("1,2", new Memory(Map.of(zero, typeOne, one, typeTwo))),
                arguments(",1", new Memory(Map.of(one, typeOne))),
                arguments(",1,2", new Memory(Map.of(one, typeOne, two, typeTwo))),
                arguments("1,,2", new Memory(Map.of(zero, typeOne, two, typeTwo))),
                arguments(",,2", new Memory(Map.of(two, typeTwo))),
                arguments(null, null)
        );
    }

}
