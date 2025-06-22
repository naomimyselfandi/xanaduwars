package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FocusCostQueryTest {

    @Mock
    private Script script;

    @Mock
    private Unit subject;

    @Mock
    private NormalAction action;

    private FocusCostQuery fixture;

    @BeforeEach
    void setup() {
        fixture = new FocusCostQuery(subject, action, List.of(new Object(), new Object()));
    }

    @Test
    void defaultValue() {
        assertThat(fixture.defaultValue()).isZero();
    }

    @Test
    void prologue() {
        when(action.getFocusCost()).thenReturn(script);
        assertThat(fixture.prologue()).containsExactly(script);
    }

}
