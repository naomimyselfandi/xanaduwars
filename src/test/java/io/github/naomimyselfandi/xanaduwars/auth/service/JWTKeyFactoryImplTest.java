package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.auth.entity.JWTKey;
import io.github.naomimyselfandi.xanaduwars.auth.entity.JWTKeyRepository;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class JWTKeyFactoryImplTest {

    private Instant expiry;

    @Mock
    private JWTKeyRepository jwtKeyRepository;

    private JWTKeyFactory fixture;

    @BeforeEach
    void setup(SeededRng random) {
        expiry = Instant.ofEpochMilli(random.nextLong());
        fixture = new JWTKeyFactoryImpl(new Random(42), jwtKeyRepository);
    }

    @EnumSource
    @ParameterizedTest
    @SuppressWarnings("SpellCheckingInspection")
    void get(JWTPurpose purpose, SeededRng random) {
        var id = new Id<JWTKey>(random.nextUUID());
        when(jwtKeyRepository.save(any()))
                .thenAnswer(invocation -> {
                    var key = invocation.<JWTKey>getArgument(0);
                    assertThat(key.getEncodedSecret()).isEqualTo("NZ1BuveK/g3hu+euKMBFDOQ8CE9Luyvxg53uRm2FLLU=");
                    assertThat(key.getPurpose()).isEqualTo(purpose);
                    assertThat(key.getExpiry()).isEqualTo(expiry);
                    return key.setId(id);
                })
                .thenThrow(AssertionError.class);
        var key = fixture.get(purpose, expiry);
        assertThat(key.getId()).isEqualTo(id);
        assertThat(key.getEncodedSecret()).isEqualTo("NZ1BuveK/g3hu+euKMBFDOQ8CE9Luyvxg53uRm2FLLU=");
        assertThat(key.getPurpose()).isEqualTo(purpose);
        assertThat(key.getExpiry()).isEqualTo(expiry);
        var inOrder = inOrder(jwtKeyRepository);
        inOrder.verify(jwtKeyRepository).awaitTransactionalLock();
        inOrder.verify(jwtKeyRepository).findExistingKey(purpose, expiry);
        inOrder.verify(jwtKeyRepository).save(key);
        verifyNoMoreInteractions(jwtKeyRepository);
    }

    @EnumSource
    @ParameterizedTest
    void get_WhenAKeyAlreadyExists_ThenReusesIt(JWTPurpose purpose) {
        var key = new JWTKey();
        when(jwtKeyRepository.findExistingKey(purpose, expiry)).thenReturn(Optional.of(key));
        assertThat(fixture.get(purpose, expiry)).isEqualTo(key);
        var inOrder = inOrder(jwtKeyRepository);
        inOrder.verify(jwtKeyRepository).awaitTransactionalLock();
        inOrder.verify(jwtKeyRepository).findExistingKey(purpose, expiry);
        verifyNoMoreInteractions(jwtKeyRepository);
    }

}
