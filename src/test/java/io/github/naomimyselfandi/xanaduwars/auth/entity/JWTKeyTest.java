package io.github.naomimyselfandi.xanaduwars.auth.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class JWTKeyTest {

    @RepeatedTest(3)
    void testToString(SeededRng random) {
        var key = new JWTKey()
                .setId(random.get())
                .setEncodedSecret(random.nextUUID().toString())
                .setPurpose(random.pick(JWTPurpose.values()))
                .setExpiry(Instant.ofEpochMilli(random.nextLong()));
        assertThat(key.toString()).doesNotContain(key.getEncodedSecret());
    }

}
