package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.UnitType;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TagSet;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TagsQueryTest {

    @Mock
    private UnitType type;

    @Mock
    private Unit unit;

    @InjectMocks
    private TagsQuery fixture;

    @RepeatedTest(2)
    void defaultValue(SeededRng random) {
        var tag = random.nextTag();
        when(type.tags()).thenReturn(TagSet.of(tag));
        when(unit.type()).thenReturn(type);
        assertThatCollection(fixture.defaultValue()).containsOnly(tag);
    }

}
