package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class PlaintextAPIKeyTest {

    private String string;
    private PlaintextAPIKey fixture;

    @BeforeEach
    void setup(SeededRng random) {
        string = random.nextUUID().toString();
        fixture = new PlaintextAPIKey(string);
    }

    @Test
    void text() {
        assertThat(fixture.text()).isEqualTo(string);
    }

    @Test
    void toHash(SeededRng random) {
        var hash = random.nextAPIKey();
        assertThat(fixture.toHash(hash.text())).isEqualTo(hash);
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("*".repeat(12));
    }

    @Test
    void json() throws JsonProcessingException {
        assertThat(new ObjectMapper().writeValueAsString(fixture)).isEqualTo("\"%s\"", fixture);
    }

}
