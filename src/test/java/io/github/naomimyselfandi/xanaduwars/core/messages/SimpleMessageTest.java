package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SimpleMessageTest {

    private record Helper(int foo, int bar) implements SimpleMessage {}

    private int foo, bar;
    private SimpleMessage fixture;

    @BeforeEach
    void setup(SeededRng random) {
        foo = random.get();
        bar = random.not(foo);
        fixture = new Helper(foo, bar);
    }

    @Test
    void type() {
        assertThat(fixture.type()).isEqualTo(new SimpleMessageType(Helper.class));
    }

    @Test
    void arguments() {
        assertThat(fixture.arguments()).isUnmodifiable().containsExactly(foo, bar);
    }

}
