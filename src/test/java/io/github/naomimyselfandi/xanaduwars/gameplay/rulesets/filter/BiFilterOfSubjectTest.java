package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class BiFilterOfSubjectTest {

    @Mock
    private BiFilter<Object, Object> filter;

    @InjectMocks
    private BiFilterOfSubject<Object, Object> fixture;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean expected) {
        var subject = new Object();
        var target = new Object();
        when(filter.test(target, subject)).thenReturn(expected);
        assertThat(fixture.test(subject, target)).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("@filter");
    }

}
