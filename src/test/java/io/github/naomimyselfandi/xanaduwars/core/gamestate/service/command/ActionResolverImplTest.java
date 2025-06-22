package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ActionResolverImplTest {

    @Mock
    private NormalAction action0, action1;

    @Mock
    private Unit actor;

    private ActionResolverImpl fixture;

    @BeforeEach
    void setup() {
        when(actor.getActions()).thenReturn(List.of(action0, action1));
        fixture = new ActionResolverImpl();
    }

    @Test
    void resolveAction(SeededRng random) throws ConflictException {
        var name0 = random.<Name>get();
        var name1 = random.<Name>get();
        when(action0.getName()).thenReturn(name0);
        when(action1.getName()).thenReturn(name1);
        assertThat(fixture.resolveAction(actor, name0)).isEqualTo(action0);
        assertThat(fixture.resolveAction(actor, name1)).isEqualTo(action1);
    }

    @Test
    void resolveAction_WhenTheActionDoesNotExist_ThenThrows(SeededRng random) {
        assertThatThrownBy(() -> fixture.resolveAction(actor, random.get())).isInstanceOf(ConflictException.class);
    }

}
