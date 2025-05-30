package io.github.naomimyselfandi.xanaduwars.account.value;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class UsernameTest {

    private Username fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new Username(random.nextUUID().toString());
    }

    @Test
    void canonicalForm() {
        assertThat(fixture.canonicalForm()).isEqualTo(new CanonicalUsername(fixture.username()));
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString(fixture.username());
    }

}
