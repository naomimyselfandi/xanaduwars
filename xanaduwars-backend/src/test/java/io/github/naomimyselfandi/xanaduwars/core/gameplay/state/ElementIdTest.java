package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class ElementIdTest {

    @ParameterizedTest
    @MethodSource("testCases")
    void json(@Language("json") String json, ElementId elementId) {
        TestUtils.assertJson(elementId, json);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void json_Polymorphism(@Language("json") String json, ElementId elementId) {
        TestUtils.assertJson(ElementId.class, elementId, json);
    }

    @ParameterizedTest
    @MethodSource("nodeTestCases")
    void json_NodePolymorphism(@Language("json") String json, NodeId nodeId) {
        TestUtils.assertJson(NodeId.class, nodeId, json);
    }

    @ParameterizedTest
    @MethodSource("actorTestCases")
    void json_ActorPolymorphism(@Language("json") String json, ActorId actorId) {
        TestUtils.assertJson(ActorId.class, actorId, json);
    }

    @ParameterizedTest
    @MethodSource("targetTestCases")
    void json_TargetPolymorphism(@Language("json") String json, AssetId assetId) {
        TestUtils.assertJson(AssetId.class, assetId, json);
    }

    private static Arguments testCase(@Language("json") String json, ElementId id) {
        return arguments(json, id);
    }

    private static Stream<Arguments> testCases() {
        return Stream.of(
                testCase("""
                {"playerId": 0}
                """, new PlayerId(0)),
                testCase("""
                {"playerId": 1}
                """, new PlayerId(1)),
                testCase("""
                {"x": 12, "y": 34}
                """, new TileId(12, 34)),
                testCase("""
                {"structureId": {"x": 56, "y": 78}}
                """, new StructureId(new TileId(56, 78))),
                testCase("""
                {"unitId": 1234}
                """, new UnitId(1234))
        );
    }

    private static Stream<Arguments> nodeTestCases() {
        return testCases().filter(arguments -> arguments.get()[1] instanceof NodeId);
    }

    private static Stream<Arguments> actorTestCases() {
        return testCases().filter(arguments -> arguments.get()[1] instanceof ActorId);
    }

    private static Stream<Arguments> targetTestCases() {
        return testCases().filter(arguments -> arguments.get()[1] instanceof AssetId);
    }

}
