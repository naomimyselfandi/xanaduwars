package io.github.naomimyselfandi.xanaduwars.core;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.classgraph.ClassGraph;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.script.Function;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import io.github.naomimyselfandi.xanaduwars.core.service.CommandService;
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

    @Autowired
    private CommandService commandService;

    private Version version;

    @BeforeEach
    void setup() {
        version = versionService.getVersion(new VersionNumber(1, 0, 0, ""));
    }

    @ParameterizedTest
    @MethodSource("getTestScripts")
    void runTestScript(TestScript script) throws CommandException {
        var gameState = gameStateFactory.create(script.width(), script.height(), script.players(), version);
        var assertThat = Script
                .of("return(@assertThat)")
                .<Function>executeNotNull(gameState, Map.of())
                .bind(Assertions.class);
        var assertNull = (Function) arguments -> {
            // SpEl has trouble calling assertThat(null) due to ambiguous conversions
            Assertions.assertThat(arguments[0]).isNull();
            return null;
        };
        for (var step : script.steps()) {
            execute(step, gameState, Map.of("assertThat", assertThat, "assertNull", assertNull));
        }
    }

    private void execute(TestScript.Step step, GameState gameState, Map<String, Object> utils)
            throws CommandException  {
        Void _ = switch (step) {
            case TestScript.Evaluate it -> {
                try {
                    it.evaluate().execute(gameState, utils);
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
                yield null;
            }
            case TestScript.Invalid it -> {
                var assertThatException = assertThatThrownBy(() -> execute(it.invalid(), gameState, utils))
                        .isInstanceOf(Objects.requireNonNullElse(it.exception(), CommandException.class));
                if (it.message() instanceof String message) {
                    for (var part : message.split("\\.\\.\\.")) {
                        assertThatException.hasMessageContaining(part);
                    }
                }
                yield null;
            }
            case TestScript.PlayerCommand it -> {
                commandService.submit(gameState, new CommandSequenceForSelf(it.cast()));
                yield null;
            }
            case TestScript.UnitCommand it -> {
                commandService.submit(gameState, new CommandSequenceForUnit(it.x(), it.y(), it.use()));
                yield null;
            }
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
