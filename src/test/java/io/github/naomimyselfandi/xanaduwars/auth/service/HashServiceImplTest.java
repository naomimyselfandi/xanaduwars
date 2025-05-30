package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_Password(boolean expected, SeededRng random) {
        var plaintext = random.nextPlaintextPassword();
        var hash = random.nextPassword();
        when(passwordEncoder.matches(plaintext.text(), hash.text())).thenReturn(expected);
        assertThat(fixture.test(plaintext, hash)).isEqualTo(expected);
    }

    @RepeatedTest(2)
    void hash_ApiKey(SeededRng random) {
        var plaintext = random.nextPlaintextAPIKey();
        var expected = random.nextAPIKey();
        when(passwordEncoder.encode(plaintext.text())).thenReturn(expected.text());
        assertThat(fixture.hash(plaintext)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_ApiKey(boolean expected, SeededRng random) {
        var plaintext = random.nextPlaintextAPIKey();
        var hash = random.nextAPIKey();
        when(passwordEncoder.matches(plaintext.text(), hash.text())).thenReturn(expected);
        assertThat(fixture.test(plaintext, hash)).isEqualTo(expected);
    }

}
