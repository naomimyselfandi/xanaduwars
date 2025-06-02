package io.github.naomimyselfandi.xanaduwars.account.value;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class AccountIdTest {

    @Test
    void testToString(SeededRng random) {
        var id = random.nextAccountId();
        assertThat(id).hasToString(id.id().toString());
    }

}
