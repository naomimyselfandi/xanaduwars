package io.github.naomimyselfandi.xanaduwars.util;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(SeededRandomExtension.class)
class IdConverterTest {

    private IdConverter fixture;

    @BeforeEach
    void setup() {
        fixture = new IdConverter();
    }

    @Test
    void convert(SeededRng random) {
        var uuid = random.nextUUID();
        assertThat(fixture.convert(uuid.toString())).isEqualTo(new Id<>(uuid));
    }

}
