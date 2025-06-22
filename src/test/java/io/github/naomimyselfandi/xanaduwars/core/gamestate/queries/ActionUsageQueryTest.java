package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ActionUsageQueryTest {

    @Mock
    private Script script;

    @Mock
    private Unit subject;
    
    @Mock
    private NormalAction action, anotherAction;

    private ActionUsageQuery fixture;

    @BeforeEach
    void setup() {
        fixture = new ActionUsageQuery(subject, action);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void defaultValue(boolean hasAction) {
        when(subject.getActions()).thenReturn(hasAction ? List.of(action, anotherAction) : List.of(anotherAction));
        assertThat(fixture.defaultValue() instanceof Result.Okay).isEqualTo(hasAction);
    }

    @Test
    void epilogue() {
        when(action.getPrecondition()).thenReturn(script);
        assertThat(fixture.epilogue()).containsExactly(script);
    }

}
