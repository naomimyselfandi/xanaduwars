package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.SubjectQuery;
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
class SubjectRuleValidatorTest {

    @Mock
    private Filter<Object> subjectFilter;

    @Mock
    private SubjectQuery.Validation<Object> query;

    private final Set<Object> valid = new HashSet<>();

    private final SubjectRule.Validator<SubjectQuery.Validation<Object>, Object> fixture = new SubjectRule.Validator<>() {

        @Override
        public boolean isValid(SubjectQuery.Validation<Object> query) {
            return valid.contains(query);
        }

        @Override
        public Filter<Object> subjectFilter() {
            return subjectFilter;
        }

    };

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void handles(boolean passesFilter, boolean invalid, boolean expected) {
        if (!invalid) valid.add(query);
        var subject = new Object();
        lenient().when(query.subject()).thenReturn(subject);
        lenient().when(subjectFilter.test(subject)).thenReturn(passesFilter);
        assertThat(fixture.handles(query, true)).isEqualTo(expected);
    }

}
