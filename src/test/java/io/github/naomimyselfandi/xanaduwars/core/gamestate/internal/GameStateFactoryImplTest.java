package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.CopyMachine;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.RulesetService;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryEvaluator;
import io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator.QueryEvaluatorFactory;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStateFactoryImplTest {

    private GameStateData gameStateData;

    @Mock
    private Ruleset ruleset;

    @Mock
    private QueryEvaluator queryEvaluator;

    @Mock
    private Redactor redactor;

    @Mock
    private CopyMachine copyMachine;

    @Mock
    private RulesetService rulesetService;

    @Mock
    private QueryEvaluatorFactory queryEvaluatorFactory;

    @InjectMocks
    private GameStateFactoryImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        gameStateData = random.get();
    }

    @Test
    void create() {
        when(rulesetService.load(gameStateData.getVersion())).thenReturn(ruleset);
        when(queryEvaluatorFactory.create(ruleset)).thenReturn(queryEvaluator);
        var game = fixture.create(gameStateData);
        assertThat(game.getRuleset()).isEqualTo(ruleset);
        assertThat(game.gameStateData).isEqualTo(gameStateData);
        assertThat(game.queryEvaluator).isEqualTo(
                new QueryEvaluatorWithCleaner(
                        new QueryEvaluatorWithCleaner(queryEvaluator, new CleanerForMemory()),
                        new CleanerForTurn()
                ));
        assertThat(game.copyMachine).isEqualTo(copyMachine);
        assertThat(game.redactor).isEqualTo(redactor);
        assertThat(game.isLimitedCopy()).isFalse();
    }

}
