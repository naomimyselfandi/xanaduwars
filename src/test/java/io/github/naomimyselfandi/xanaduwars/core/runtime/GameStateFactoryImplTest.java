package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.VersionService;
import io.github.naomimyselfandi.xanaduwars.core.data.GameData;
import io.github.naomimyselfandi.xanaduwars.core.data.GameDataFactory;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.VersionNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStateFactoryImplTest {

    @Mock
    private Ruleset ruleset;

    @Mock
    private VersionService versionService;

    @Mock
    private GameDataFactory gameDataFactory;

    @Mock
    private ActionPolicy actionPolicy;

    @Mock
    private ActionExecutor actionExecutor;

    @InjectMocks
    private GameStateFactoryImpl fixture;

    @Test
    void create(SeededRandom random) {
        var versionNumber = new VersionNumber("%d.%d.%d".formatted(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255)
        ));
        when(versionService.load(versionNumber)).thenReturn(ruleset);
        var data = new GameData().versionNumber(versionNumber);
        data.width(random.nextInt(1, 255)); // Prevent division by zero
        var evaluator = new QueryEvaluatorImpl(ruleset);
        var expected = new GameStateImpl(data, ruleset, gameDataFactory, evaluator, actionPolicy, actionExecutor);
        assertThat(fixture.create(data)).isEqualTo(expected);
    }

}
