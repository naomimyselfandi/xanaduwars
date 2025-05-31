package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JWTClaimTest {

    @Test
    void none() {
        assertThat(JWTClaim.NONE.claims()).isEmpty();
    }

}
