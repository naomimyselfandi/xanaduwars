package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.BiFilter;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TargetQuery;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TargetRuleValidatorTest {

    @Mock
    private Filter<Object> subjectFilter;

    @Mock
    private BiFilter<Object, Object> targetFilter;

    @Mock
    private TargetQuery.Validation<Object, Object> query;

    private final Set<Object> valid = new HashSet<>();

    private final TargetRule.Validator<TargetQuery.Validation<Object, Object>, Object, Object> fixture
            = new TargetRule.Validator<>() {

        @Override
        public boolean isValid(TargetQuery.Validation<Object, Object> query) {
            return valid.contains(query);
        }

        @Override
        public Filter<Object> subjectFilter() {
            return subjectFilter;
        }

        @Override
        public BiFilter<Object, Object> targetFilter() {
            return targetFilter;
        }

    };

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void handles(boolean passesSubjectFilter, boolean passesTargetFilter, boolean invalid, boolean expected) {
        if (!invalid) valid.add(query);
        var subject = new Object();
        var target = new Object();
        lenient().when(query.subject()).thenReturn(subject);
        lenient().when(subjectFilter.test(subject)).thenReturn(passesSubjectFilter);
        lenient().when(query.target()).thenReturn(target);
        lenient().when(targetFilter.test(subject, target)).thenReturn(passesTargetFilter);
        assertThat(fixture.handles(query, true)).isEqualTo(expected);
    }

}
