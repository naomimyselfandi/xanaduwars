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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyCostQueryTest {

    @Mock
    private Script script;

    @Mock
    private Unit subject;

    @Mock
    private NormalAction action;

    private SupplyCostQuery fixture;

    @BeforeEach
    void setup() {
        fixture = new SupplyCostQuery(subject, action, List.of(new Object(), new Object()));
    }

    @Test
    void defaultValue() {
        assertThat(fixture.defaultValue()).isZero();
    }

    @Test
    void prologue() {
        when(action.getSupplyCost()).thenReturn(script);
        assertThat(fixture.prologue()).containsExactly(script);
    }

}
