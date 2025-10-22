package io.github.naomimyselfandi.xanaduwars.auth.value;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class PasswordResetTokenTest {

    @Test
    void testToString(SeededRng random) {
        assertThat(random.<PasswordResetToken>get()).hasToString("PasswordResetToken[token=************]");
    }

}
