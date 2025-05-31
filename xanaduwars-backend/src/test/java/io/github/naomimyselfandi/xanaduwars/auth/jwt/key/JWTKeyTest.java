package io.github.naomimyselfandi.xanaduwars.auth.jwt.key;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class JWTKeyTest {

    @RepeatedTest(3)
    void testToString(SeededRng random) {
        var key = new JWTKey()
                .id(random.nextUUID())
                .encodedSecret(random.nextUUID().toString())
                .purpose(random.pick(JWTPurpose.values()))
                .expiry(Instant.ofEpochMilli(random.nextLong()));
        assertThat(key.toString()).doesNotContain(key.encodedSecret());
    }

}
