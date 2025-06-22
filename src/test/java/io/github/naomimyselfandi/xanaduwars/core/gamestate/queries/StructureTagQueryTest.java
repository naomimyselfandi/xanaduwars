package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.StructureTag;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Structure;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.StructureType;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StructureTagQueryTest {

    @Mock
    private StructureType type;

    @Mock
    private Structure subject;

    @InjectMocks
    private StructureTagQuery fixture;

    @Test
    void defaultValue(SeededRng random) {
        var tags = Set.<StructureTag>of(random.get(), random.get());
        when(subject.getType()).thenReturn(type);
        when(type.getTags()).thenReturn(tags);
        assertThat(fixture.defaultValue()).isEqualTo(tags);
    }

}
