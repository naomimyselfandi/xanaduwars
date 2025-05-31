package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.RulesetService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.GameData;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.GameDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStateFactoryImplTest {

    @Mock
    private Ruleset ruleset;

    @Mock
    private RulesetService rulesetService;

    @Mock
    private GameDataFactory gameDataFactory;

    @Mock
    private ActionPolicy actionPolicy;

    @Mock
    private ActionExecutor actionExecutor;

    @InjectMocks
    private GameStateFactoryImpl fixture;

    @Test
    void create(SeededRng random) {
        var version = random.nextVersion();
        when(rulesetService.load(version)).thenReturn(Optional.of(ruleset));
        var data = new GameData().version(version);
        data.width(random.nextInt(1, 255)); // Prevent division by zero
        var evaluator = new QueryEvaluatorImpl(ruleset);
        var expected = new GameStateImpl(data, ruleset, gameDataFactory, evaluator, actionPolicy, actionExecutor);
        assertThat(fixture.create(data)).isEqualTo(expected);
    }

}
