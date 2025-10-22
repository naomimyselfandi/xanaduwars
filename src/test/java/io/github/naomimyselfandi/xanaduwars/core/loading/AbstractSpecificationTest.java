package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.message.Rule;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AbstractSpecificationTest {

    private static class Helper extends AbstractSpecification {}

    private Helper fixture;

    @BeforeEach
    void setup() {
        fixture = new Helper();
    }

    @Test
    void getContextualRules(SeededRng random) {
        var rule = random.<Rule>get();
        assertThat(fixture.setRules(List.of(rule))).isSameAs(fixture);
        assertThat(fixture.getContextualRules()).containsExactly(rule);
    }

    @Test
    void testToString(SeededRng random) {
        var name = random.nextString();
        var helper = new Helper().setName(name);
        assertThat(helper).hasToString("Helper[name=%s]", name);
    }

}
