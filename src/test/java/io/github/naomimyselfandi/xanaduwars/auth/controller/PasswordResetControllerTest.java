package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.auth.service.PasswordResetService;
import io.github.naomimyselfandi.xanaduwars.auth.value.PasswordResetToken;
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
class PasswordResetControllerTest {

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private PasswordResetController fixture;

    @Test
    void createResetToken(SeededRng random) {
        var emailAddress = random.<EmailAddress>get();
        assertThat(fixture.createResetToken(emailAddress)).isEqualTo(ResponseEntity.noContent().build());
        verify(passwordResetService).createResetTokenForUserRequest(emailAddress);
    }

    @Test
    void validateResetToken(SeededRng random) {
        var token = random.<PasswordResetToken>get();
        var account = random.<UserDetailsDto>get();
        when(passwordResetService.validateResetToken(token)).thenReturn(Optional.of(account));
        assertThat(fixture.validateResetToken(token)).isEqualTo(ResponseEntity.ok(account));
    }

    @Test
    void validateResetToken_WhenTheResetTokenIsInvalid_ThenReturnsA404(SeededRng random) {
        var token = random.<PasswordResetToken>get();
        when(passwordResetService.validateResetToken(token)).thenReturn(Optional.empty());
        assertThat(fixture.validateResetToken(token)).isEqualTo(ResponseEntity.notFound().build());
    }

    @Test
    void useResetToken(SeededRng random) {
        var token = random.<PasswordResetToken>get();
        var password = random.<PasswordResetRequest>get();
        assertThat(fixture.useResetToken(token, password)).isEqualTo(ResponseEntity.noContent().build());
        verify(passwordResetService).resetPassword(token, password.password());
    }

}
