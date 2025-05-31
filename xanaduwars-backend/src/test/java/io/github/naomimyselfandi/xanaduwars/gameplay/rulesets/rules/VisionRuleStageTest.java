package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.VisionQueryStage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VisionRuleStageTest {

    @Mock
    private Unit subject, target;

    @Mock
    private Filter<Node> subjectFilter;

    @Mock
    private BiFilter<Node, Node> targetFilter, visionFilter;

    private VisionRuleStage fixture;

    @BeforeEach
    void setup() {
        fixture = new VisionRuleStage(subjectFilter, targetFilter, visionFilter);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isValid(boolean expected) {
        when(visionFilter.test(subject, target)).thenReturn(expected);
        assertThat(fixture.isValid(new VisionQueryStage(subject, target))).isEqualTo(expected);
    }

}
