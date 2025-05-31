package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Tag;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TagSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BiFilterOfTagTest {

    private final Tag spam = new Tag("Spam"), eggs = new Tag("Eggs");

    @Mock
    private Unit foo, bar;

    private final BiFilterOfTag<Unit, Unit> fixture = new BiFilterOfTag<>(spam);

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean expected) {
        when(foo.tags()).thenReturn(expected ? TagSet.of(spam, eggs) : TagSet.of(eggs));
        assertThat(fixture.test(bar, foo)).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("tag.Spam");
    }

}
