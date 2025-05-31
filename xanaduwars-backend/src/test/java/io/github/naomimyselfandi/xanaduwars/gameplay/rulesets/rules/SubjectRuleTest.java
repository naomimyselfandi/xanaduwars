package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.SubjectQuery;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectRuleTest {

    @Mock
    private Filter<Object> subjectFilter;

    @Mock
    private SubjectQuery<Object, Object> query;

    private final SubjectRule<SubjectQuery<Object, Object>, Object, Object> fixture = new SubjectRule<>() {

        @Override
        public Filter<Object> subjectFilter() {
            return subjectFilter;
        }

        @Override
        public Object handle(SubjectQuery<Object, Object> query, Object value) {
            throw new UnsupportedOperationException();
        }

    };

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void handles(boolean expected) {
        var subject = new Object();
        when(query.subject()).thenReturn(subject);
        when(subjectFilter.test(subject)).thenReturn(expected);
        assertThat(fixture.handles(query, new Object())).isEqualTo(expected);
    }

}
