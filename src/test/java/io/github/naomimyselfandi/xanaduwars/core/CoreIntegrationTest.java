package io.github.naomimyselfandi.xanaduwars.core;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.classgraph.ClassGraph;
import io.github.naomimyselfandi.xanaduwars.core.model.CommandException;
import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import io.github.naomimyselfandi.xanaduwars.core.model.Version;
import io.github.naomimyselfandi.xanaduwars.core.model.VersionNumber;
import io.github.naomimyselfandi.xanaduwars.core.script.Function;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import io.github.naomimyselfandi.xanaduwars.core.service.GameStateFactory;
import io.github.naomimyselfandi.xanaduwars.core.service.VersionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.EvaluationException;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = CoreTestApplication.class)
@TestPropertySource(properties = {
        "xanadu.game.root=simpleTestVersion"
})
class CoreIntegrationTest {

    @Autowired
    private VersionService versionService;

    @Autowired
    private GameStateFactory gameStateFactory;

    private Version version;

    @BeforeEach
    void setup() {
        version = versionService.getVersion(new VersionNumber(1, 0, 0, ""));
    }

    @ParameterizedTest
    @MethodSource("getTestScripts")
    void runTestScript(TestScript script) throws CommandException {
        var gameState = gameStateFactory.create(script.width(), script.height(), script.players(), version);
        var assertion = Script.of("return(@assertThat)").<Function>executeNotNull(gameState, Map.of());
        for (var step : script.steps()) {
            execute(step, gameState, arguments -> {
                var actual = switch (arguments[0]) {
                    case Optional<?> opt -> opt.orElse(null);
                    case OptionalDouble opt -> opt.stream().boxed().findFirst().orElse(null);
                    case OptionalLong opt -> opt.stream().boxed().findFirst().orElse(null);
                    case OptionalInt opt -> opt.stream().boxed().findFirst().orElse(null);
                    case null, default -> arguments[0];
                };
                return assertion.call(Assertions.class, actual);
            });
        }
    }

    private static void execute(TestScript.Step step, GameState gameState, Function assertThat) throws CommandException  {
        var _ = switch (step) {
            case TestScript.Evaluate it -> {
                try {
                    yield it.evaluate().execute(gameState, Map.of("assertThat", assertThat));
                } catch (EvaluationException e) {
                    Throwable cause = e.getCause();
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    if (cause instanceof AssertionError a) {
                        throw new AssertionError(e.getMessage(), a);
                    } else {
                        throw e;
                    }
                }
            }
            case TestScript.Invalid it -> {
                var assertThatException = assertThatThrownBy(() -> execute(it.invalid(), gameState, assertThat))
                        .isInstanceOf(Objects.requireNonNullElse(it.exception(), CommandException.class));
                if (it.message() instanceof String message) {
                    for (var part : message.split("\\.\\.\\.")) {
                        assertThatException.hasMessageContaining(part);
                    }
                }
                yield null;
            }
            case TestScript.PlayerCommand it -> gameState.submitPlayerCommand(it.cast());
            case TestScript.UnitCommand it -> gameState.submitUnitCommand(it.x(), it.y(), it.use());
        };
    }

    private static Stream<TestScript> getTestScripts() throws IOException {
        var objectMapper = new ObjectMapper().enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.mappedFeature());
        var result = Stream.<TestScript>builder();
        var base = "coreTestScripts/";
        try (var scan = new ClassGraph().acceptPaths(base).scan()) {
            for (var resource : scan.getResourcesWithExtension(".json")) {
                var name = resource.getPath().substring(base.length()).replaceFirst(".json$", "");
                var builder = objectMapper.readValue(resource.getContentAsString(), TestScript.TestScriptBuilder.class);
                var script = builder.setName(name).build();
                result.accept(script);
            }
        }
        return result.build();
    }

}
