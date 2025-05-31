package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class BiFilterYesTest {

    @Mock
    private Unit foo, bar;

    private final BiFilterYes<Unit, Unit> fixture = new BiFilterYes<>();

    @Test
    void test() {
        assertThat(fixture.test(bar, foo)).isTrue();
        verifyNoInteractions(foo, bar);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("*");
    }

}
