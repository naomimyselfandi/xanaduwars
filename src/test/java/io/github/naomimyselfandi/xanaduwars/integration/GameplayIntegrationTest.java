package io.github.naomimyselfandi.xanaduwars.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.ChoiceService;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.GameStateDataService;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.GameStateFactory;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command.CommandProcessor;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.VersionService;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import io.github.naomimyselfandi.xanaduwars.util.InvalidOperationException;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Rollback(false)
@TestPropertySource(properties = "xanadu.core.ruleset.root=testRuleset")
public class GameplayIntegrationTest extends AbstractIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private static final StandardTypeLocator TYPE_LOCATOR = new StandardTypeLocator();

    static {
        TYPE_LOCATOR.registerImport("io.github.naomimyselfandi.xanaduwars.core");
        TYPE_LOCATOR.registerImport("io.github.naomimyselfandi.xanaduwars.core.common");
        TYPE_LOCATOR.registerImport("io.github.naomimyselfandi.xanaduwars.core.gamestate");
    }

    private EvaluationContext evaluationContext;

    @Autowired
    private GameStateDataService gameStateDataService;

    @Autowired
    private GameStateFactory gameStateFactory;

    @Autowired
    private CommandProcessor commandProcessor;

    @Autowired
    private ChoiceService choiceService;

    @Autowired
    private VersionService versionService;

    @ParameterizedTest
    @MethodSource("testScripts")
    void executeTestScript(GameplayTestScript testScript) throws InvalidOperationException {
        var data = gameStateDataService.create(testScript.getMap(), versionService.published().getFirst());
        var game = gameStateFactory.create(data);
        for (var i = 0 ; i < testScript.getChoices().size(); i++) {
            choiceService.setChoices(game, new PlayerId(i), testScript.getChoices().get(i));
        }
        game.setTurn(new Turn(0));
        createEvaluationContext(game);
        testScript.getPreconditions().forEach(this::verify);
        for (var step : testScript.getSteps()) {
            TestTransaction.flagForCommit();
            TestTransaction.end();
            TestTransaction.start();
            step.getSetup().forEach(this::evaluate);
            var command = step.getCommand();
            if (step.isInvalid()) {
                assertThatThrownBy(() -> commandProcessor.process(game, command)).isInstanceOf(ConflictException.class);
            } else {
                assertThatCode(() -> commandProcessor.process(game, command)).doesNotThrowAnyException();
            }
            step.getPostconditions().forEach(this::verify);
        }
    }

    private static List<GameplayTestScript> testScripts() {
        try (var scan = new ClassGraph().acceptPaths("testScripts").scan()) {
            return scan.getResourcesWithExtension("json").stream().map(GameplayIntegrationTest::createScripts).toList();
        }
    }

    @SneakyThrows
    private static GameplayTestScript createScripts(Resource resource) {
        System.err.println(resource.getPath());
        return OBJECT_MAPPER
                .readValue(resource.getContentAsString(), GameplayTestScript.class)
                .setName(resource.getPath().replaceAll("^testScripts/|\\.json$", ""));
    }

    private void createEvaluationContext(GameState root) {
        var context = new StandardEvaluationContext(root);
        context.addMethodResolver(GameplayIntegrationTest::getHelper);
        context.setTypeLocator(TYPE_LOCATOR);
        evaluationContext = context;
    }

    private void verify(String lhs, String rhs) {
        var actual = evaluate(lhs);
        var expected = evaluate(rhs);
        assertThat(actual).describedAs("Condition `%s == %s` failed.", lhs, rhs).isEqualTo(expected);
    }

    private @Nullable Object evaluate(String expression) {
        var parsed = EXPRESSION_PARSER.parseExpression(expression);
        try {
            return parsed.getValue(evaluationContext);
        } catch (Exception e) {
            return fail("Failed evaluating " + expression, e);
        }
    }

    private static @Nullable MethodExecutor getHelper(
            EvaluationContext context,
            Object targetObject,
            String name,
            List<TypeDescriptor> argumentTypes
    ) {
        return targetObject instanceof GameState gameState ? switch (name) {
            case "player" -> (_, _, a) -> {
                var player = gameState.getPlayers().get((Integer) a[0]);
                return new TypedValue(player);
            };
            case "tile" -> (_, _, a) -> {
                var tileId = new TileId((Integer) a[0], (Integer) a[1]);
                var tile = gameState.getTiles().get(tileId);
                return new TypedValue(tile);
            };
            case "unit" -> (_, _, a) -> {
                if (a.length == 2) {
                    var tileId = new TileId((Integer) a[0], (Integer) a[1]);
                    var tile = gameState.getTiles().get(tileId);
                    return new TypedValue(tile.getUnit());

                } else {
                    var unitId = new UnitId((Integer) a[0]);
                    var unit = gameState.getUnits().get(unitId);
                    return new TypedValue(unit);
                }
            };
            default -> null;
        } : null;
    }

}
