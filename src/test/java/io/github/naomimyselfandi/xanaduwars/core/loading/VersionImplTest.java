package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.message.Rule;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VersionImplTest {

    private final VersionImpl fixture = new VersionImpl();

    @RepeatedTest(3)
    void accept(SeededRng random) {
        var fooName = random.nextString();
        var barName = random.nextString();
        var foo = new Object();
        var bar = new Object();
        fixture.accept(fooName, foo);
        fixture.accept(barName, bar);
        assertThat(fixture.getDeclarations()).containsExactly(foo, bar);
        assertThat(fixture.lookup(fooName)).isEqualTo(foo);
        assertThat(fixture.lookup(barName)).isEqualTo(bar);
        assertThat(fixture.lookup(random.nextString())).isNull();
    }

    @Test
    void accept_WhenANameIsUsedMoreThanOnce_ThenThrows(SeededRng random) {
        var name = random.nextString();
        var foo = new Object();
        var bar =new Object();
        fixture.accept(name, foo);
        assertThatThrownBy(() -> fixture.accept(name, bar))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Got multiple declarations with the same name: %s and %s", foo, bar);
    }

    @Test
    void setGlobalRules(SeededRng random) {
        var rule1 = random.<Rule>get();
        var rule2 = random.<Rule>get();
        var globalRules = new ArrayList<>(List.of(rule1, rule2));
        fixture.setGlobalRules(globalRules);
        assertThat(fixture.getGlobalRules()).isUnmodifiable().containsExactly(rule1, rule2);
        globalRules.clear(); // Ensure a full copy was taken
        assertThat(fixture.getGlobalRules()).isUnmodifiable().containsExactly(rule1, rule2);
    }

}
