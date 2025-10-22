package io.github.naomimyselfandi.xanaduwars.auth.value;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UnauthorizedExceptionTest {

    @Test
    void constructor() {
        assertThat(new UnauthorizedException()).hasMessage(UnauthorizedException.INVALID_CREDENTIALS);
    }

}
