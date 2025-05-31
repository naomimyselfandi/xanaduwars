package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class HashServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private HashServiceImpl fixture;

    @RepeatedTest(2)
    void hash_Password(SeededRng random) {
        var plaintext = random.nextPlaintextPassword();
        var expected = random.nextPassword();
        when(passwordEncoder.encode(plaintext.text())).thenReturn(expected.text());
        assertThat(fixture.hash(plaintext)).isEqualTo(expected);
    }

    @RepeatedTest(2)
    void hash_ApiKey(SeededRng random) {
        var plaintext = random.nextPlaintextAPIKey();
        var expected = random.nextAPIKey();
        when(passwordEncoder.encode(plaintext.text())).thenReturn(expected.text());
        assertThat(fixture.hash(plaintext)).isEqualTo(expected);
    }

}
