package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.NodeId;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.TileId;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.UnitId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.Nullable;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class NodeIdConverterTest {

    private NodeIdConverter fixture;

    @BeforeEach
    void setup() {
        fixture = new NodeIdConverter();
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void convertToDatabaseColumn(@Nullable String dbData, @Nullable NodeId attribute) {
        assertThat(fixture.convertToDatabaseColumn(attribute)).isEqualTo(dbData);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void convertToEntityAttribute(@Nullable String dbData, @Nullable NodeId attribute) {
        assertThat(fixture.convertToEntityAttribute(dbData)).isEqualTo(attribute);
    }

    private static Stream<Arguments> testCases() {
        return Stream.of(
                arguments("000,000", new TileId(0, 0)),
                arguments("123,345", new TileId(123, 345)),
                arguments("567,789", new TileId(567, 789)),
                arguments("0000000", new UnitId(0)),
                arguments("0000001", new UnitId(1)),
                arguments("0000009", new UnitId(9)),
                arguments("0000010", new UnitId(10)),
                arguments("0123456", new UnitId(123456)),
                arguments("1234567", new UnitId(1234567)),
                arguments(null, null)
        );
    }

}
