package io.github.naomimyselfandi.xanaduwars.util;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class NotFoundExceptionTest {

    @Test
    void constructor(SeededRng random) {
        interface Helper {}
        var id = random.<Id<Helper>>get();
        assertThat(new NotFoundException(id)).hasMessage("No Helper with ID %s.", id);
    }

}
