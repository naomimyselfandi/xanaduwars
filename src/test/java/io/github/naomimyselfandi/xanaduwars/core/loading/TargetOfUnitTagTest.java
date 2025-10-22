package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitTag;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetOfUnitTagTest {

    @Mock
    private Unit actor, target;

    @Mock
    private Target<Unit> base;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        this.random = random;
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.OR)
    void validateFurther(boolean tag1Matches, boolean tag2Matches, boolean expected) {
        var tag1 = random.<UnitTag>get();
        var tag2 = random.not(tag1);
        when(target.getTags()).thenReturn(random.shuffle(tag1, tag2));
        var fixture = new TargetOfUnitTag(base, List.of(
                tag1Matches ? tag1 : random.not(tag1, tag2),
                tag2Matches ? tag2 : random.not(tag1, tag2)
        ));
        assertThat(fixture.validateFurther(actor, target)).isEqualTo(expected);
        verifyNoInteractions(actor);
    }

}
