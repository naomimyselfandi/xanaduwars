package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class NodeIdTest {

    @MethodSource
    @ParameterizedTest
    void withIntValue(int intValue, NodeId expected) {
        assertThat(NodeId.withIntValue(intValue)).isEqualTo(expected);
    }

    private static Stream<Arguments> withIntValue() {
        return Stream.of(
                new UnitId(0),
                new UnitId(1),
                new UnitId(5),
                new UnitId(500),
                new TileId(0, 0),
                new TileId(1, 0),
                new TileId(0, 1),
                new TileId(1, 999),
                new TileId(999, 1),
                new TileId(999, 999)
        ).map(id -> arguments(id.intValue(), id));
    }

}
