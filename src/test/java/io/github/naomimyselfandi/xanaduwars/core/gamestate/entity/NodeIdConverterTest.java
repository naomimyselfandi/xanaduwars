package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.NodeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.TileId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.UnitId;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
    void convertToDatabaseColumn(@Nullable NodeId entityAttribute, @Nullable String dbData) {
        assertThat(fixture.convertToDatabaseColumn(entityAttribute)).isEqualTo(dbData);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void convertToEntityAttribute(@Nullable NodeId entityAttribute, @Nullable String dbData) {
        assertThat(fixture.convertToEntityAttribute(dbData)).isEqualTo(entityAttribute);
    }

    private static Stream<Arguments> testCases() {
        return Stream.of(
                arguments(new TileId(413, 612), "0413,0612"),
                arguments(new UnitId(42), "Unit,0042"),
                arguments(null, null)
        );
    }

}
