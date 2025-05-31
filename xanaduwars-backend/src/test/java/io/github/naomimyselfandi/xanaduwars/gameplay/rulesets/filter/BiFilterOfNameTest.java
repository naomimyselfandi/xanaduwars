package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BiFilterOfNameTest {

    private final Name spam = new Name("Spam"), eggs = new Name("Eggs");

    @Mock
    private Unit foo, bar;

    private final BiFilterOfName<Unit, Unit> fixture = new BiFilterOfName<>(spam);

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean expected) {
        when(foo.name()).thenReturn(expected ? spam : eggs);
        assertThat(fixture.test(bar, foo)).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("name.Spam");
    }

}
