package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.SpellId;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SpellSlotListConverterTest {

    private SpellSlotListConverter fixture;

    @BeforeEach
    void setup() {
        fixture = new SpellSlotListConverter();
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void convertToDatabaseColumn(@Nullable String dbData, @Nullable SpellSlotList attribute) {
        assertThat(fixture.convertToDatabaseColumn(attribute)).isEqualTo(dbData);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void convertToEntityAttribute(@Nullable String dbData, @Nullable SpellSlotList attribute) {
        assertThat(fixture.convertToEntityAttribute(dbData)).isEqualTo(attribute);
    }

    private static Stream<Arguments> testCases() {
        var one = new SpellId(1);
        var two = new SpellId(2);
        return Stream.of(
                arguments("", new SpellSlotList(List.of())),
                arguments("1?0", new SpellSlotList(List.of(new SpellSlotData(one, false, 0)))),
                arguments("1!0", new SpellSlotList(List.of(new SpellSlotData(one, true, 0)))),
                arguments("2?1", new SpellSlotList(List.of(new SpellSlotData(two, false, 1)))),
                arguments("2!1", new SpellSlotList(List.of(new SpellSlotData(two, true, 1)))),
                arguments("1!1,2?3", new SpellSlotList(List.of(
                        new SpellSlotData(one, true, 1),
                        new SpellSlotData(two, false, 3)
                ))),
                arguments(null, null)
        );
    }

}
