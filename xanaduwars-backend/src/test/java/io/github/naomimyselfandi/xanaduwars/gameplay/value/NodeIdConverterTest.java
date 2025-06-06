package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class NodeIdConverterTest {

    private final NodeIdConverter fixture = new NodeIdConverter();

    @ParameterizedTest
    @MethodSource("testCases")
    void convertToEntityAttribute(@Nullable NodeId entityAttribute, @Nullable String dbData) {
        assertThat(fixture.convertToEntityAttribute(dbData)).isEqualTo(entityAttribute);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void convertToDatabaseColumn(@Nullable NodeId entityAttribute, @Nullable String dbData) {
        assertThat(fixture.convertToDatabaseColumn(entityAttribute)).isEqualTo(dbData);
    }

    private static Stream<Arguments> testCases() {
        return Stream.of(
                arguments(null, null),
                arguments(new UnitId(0), "0"),
                arguments(new UnitId(1), "1"),
                arguments(new UnitId(2), "2"),
                arguments(new TileId(0, 0), "0,0"),
                arguments(new TileId(1, 0), "1,0"),
                arguments(new TileId(0, 1), "0,1"),
                arguments(new TileId(1, 1), "1,1"),
                arguments(new TileId(999, 999), "999,999")
        );
    }

}
