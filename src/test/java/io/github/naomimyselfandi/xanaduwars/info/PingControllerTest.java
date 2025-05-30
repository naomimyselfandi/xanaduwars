package io.github.naomimyselfandi.xanaduwars.info;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;

class PingControllerTest {

    @Test
    void ping() {
        assertThat(new PingController().ping()).isEqualTo(ResponseEntity.ok("Pong!"));
    }

}
