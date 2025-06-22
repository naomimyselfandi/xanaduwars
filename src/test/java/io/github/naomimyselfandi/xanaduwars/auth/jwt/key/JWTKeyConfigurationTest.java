package io.github.naomimyselfandi.xanaduwars.auth.jwt.key;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.SecureRandom;
import java.time.Clock;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class JWTKeyConfigurationTest {

    @Mock
    private JWTKeyRepository jwtKeyRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private JWTKeyConfiguration fixture;

    @Test
    void jwtKeyService(SeededRng random) {
        var fudgeFactor = random.nextDouble();
        var secureRandom = new SecureRandom();
        var creator = new JWTKeyCreator(secureRandom, jwtKeyRepository);
        var expected = new JWTKeyServiceImpl(jwtKeyRepository, clock, creator);
        var actual = fixture.jwtKeyService(secureRandom, jwtKeyRepository, clock);
        assertThat(actual).isEqualTo(expected);
    }

}
