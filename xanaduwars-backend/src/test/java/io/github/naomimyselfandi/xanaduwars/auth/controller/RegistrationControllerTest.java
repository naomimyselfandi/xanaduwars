package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Password;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.dto.Registration;
import io.github.naomimyselfandi.xanaduwars.auth.service.HashService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RegistrationControllerTest {

    @Mock
    private HashService hashService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private RegistrationController fixture;

    @Test
    void register(SeededRng random) {
        var username = random.nextUsername();
        var emailAddress = random.nextEmailAddress();
        var plaintextPassword = random.nextPlaintextPassword();
        var password = random.nextPassword();
        when(hashService.hash(plaintextPassword)).thenReturn(password);
        when(accountService.create(any(), any(), any(Password.class))).then(invocation -> {
            assertThat(invocation.<Username>getArgument(0)).isEqualTo(username);
            assertThat(invocation.<EmailAddress>getArgument(1)).isEqualTo(emailAddress);
            assertThat(invocation.<Password>getArgument(2)).isEqualTo(password);
            return Optional.of(new HumanAccount());
        });
        assertThat(fixture.register(new Registration(username, emailAddress, plaintextPassword)))
                .isEqualTo(ResponseEntity.noContent().build());
        verify(accountService).create(username, emailAddress, password);
    }

}
