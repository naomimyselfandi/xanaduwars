package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Password;
import io.github.naomimyselfandi.xanaduwars.account.value.PlaintextPassword;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.dto.Registration;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RegistrationControllerTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private RegistrationController fixture;

    @Test
    void register(SeededRng random) {
        var username = random.<Username>get();
        var emailAddress = random.<EmailAddress>get();
        var plaintextPassword = random.<PlaintextPassword>get();
        var password = random.<Password>get();
        when(passwordEncoder.encode(plaintextPassword.text())).thenReturn(password.text());
        doAnswer(invocation -> {
            assertThat(invocation.<Username>getArgument(0)).isEqualTo(username);
            assertThat(invocation.<EmailAddress>getArgument(1)).isEqualTo(emailAddress);
            assertThat(invocation.<Password>getArgument(2)).isEqualTo(password);
            return null;
        }).when(accountService).create(any(), any(), any(Password.class));
        assertThat(fixture.register(new Registration(username, emailAddress, plaintextPassword)))
                .isEqualTo(ResponseEntity.noContent().build());
        verify(accountService).create(username, emailAddress, password);
    }

}
