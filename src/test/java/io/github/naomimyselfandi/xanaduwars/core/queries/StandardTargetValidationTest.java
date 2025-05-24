package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Action;
import io.github.naomimyselfandi.xanaduwars.core.Element;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.actions.ActionWithFilter;
import io.github.naomimyselfandi.xanaduwars.core.filter.BiFilter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StandardTargetValidationTest {

    @Mock
    private Action<Element, Object> action;

    @Mock
    private ActionWithFilter<Element, Object> actionWithFilter;

    @Mock
    private BiFilter<Element, Object> filter;

    @Mock
    private Unit subject;

    @Mock
    private Object target;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void defaultValue(boolean expected) {
        when(action.test(subject, target)).thenReturn(expected);
        var validation = new StandardTargetValidation<>(action, subject, target);
        assertThat(validation.defaultValue()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true,true
            false,true,false
            true,false,false
            false,false,false
            """)
    void defaultValue(boolean structurallyValid, boolean passesFilter, boolean expected) {
        when(actionWithFilter.test(subject, target)).thenReturn(structurallyValid);
        lenient().when(actionWithFilter.filter()).thenReturn(filter);
        lenient().when(filter.test(subject, target)).thenReturn(passesFilter);
        var validation = new StandardTargetValidation<>(actionWithFilter, subject, target);
        assertThat(validation.defaultValue()).isEqualTo(expected);
    }

}
