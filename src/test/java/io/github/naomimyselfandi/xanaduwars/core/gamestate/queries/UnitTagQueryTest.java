package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.UnitTag;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.UnitType;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitTagQueryTest {

    @Mock
    private UnitType type;

    @Mock
    private Unit subject;

    @InjectMocks
    private UnitTagQuery fixture;

    @Test
    void defaultValue(SeededRng random) {
        var tags = Set.<UnitTag>of(random.get(), random.get());
        when(subject.getType()).thenReturn(type);
        when(type.getTags()).thenReturn(tags);
        assertThat(fixture.defaultValue()).isEqualTo(tags);
    }

}
