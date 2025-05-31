package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TurnStartEventForUnit;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitReadyRuleTest {

    @Mock
    private Unit unit;

    @InjectMocks
    private TurnStartEventForUnit event;

    @Mock
    private Filter<Unit> filter;

    @InjectMocks
    private UnitReadyRule fixture;

    @Test
    void handle() {
        assertThat(fixture.handle(event, None.NONE)).isEqualTo(None.NONE);
        verify(unit).canAct(true);
        verifyNoInteractions(filter);
    }

}
