package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class CustomEventTypeTest {

    private String name;

    private CustomEventType fixture;

    @BeforeEach
    void setup(SeededRng random) {
        name = random.nextString();
        fixture = new CustomEventType(name, List.of(random.not(name)));
    }

    @Test
    void call() {
        var value = new Object();
        assertThat(fixture.call(value)).isEqualTo(new CustomEvent(fixture, List.of(value)));
        assertThat(fixture.call((Object) null))
                .isEqualTo(new CustomEvent(fixture, Collections.singletonList(null)));
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString(name);
    }

}
