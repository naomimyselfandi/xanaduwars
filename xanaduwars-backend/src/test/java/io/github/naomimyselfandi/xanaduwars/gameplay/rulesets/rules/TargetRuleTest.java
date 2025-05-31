package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TargetQuery;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetRuleTest {

    @Mock
    private Filter<Object> subjectFilter;

    @Mock
    private BiFilter<Object, Object> targetFilter;

    @Mock
    private TargetQuery<Object, Object, Object> query;

    private final TargetRule<TargetQuery<Object, Object, Object>, Object, Object, Object> fixture = new TargetRule<>() {

        @Override
        public Filter<Object> subjectFilter() {
            return subjectFilter;
        }

        @Override
        public BiFilter<Object, Object> targetFilter() {
            return targetFilter;
        }

        @Override
        public Object handle(TargetQuery<Object, Object, Object> query, Object value) {
            throw new UnsupportedOperationException();
        }

    };

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void handles(boolean passesSubjectFilter, boolean passesTargetFilter, boolean expected) {
        var subject = new Object();
        var target = new Object();
        when(query.subject()).thenReturn(subject);
        when(subjectFilter.test(subject)).thenReturn(passesSubjectFilter);
        lenient().when(query.target()).thenReturn(target);
        lenient().when(targetFilter.test(subject, target)).thenReturn(passesTargetFilter);
        assertThat(fixture.handles(query, new Object())).isEqualTo(expected);
    }

}
