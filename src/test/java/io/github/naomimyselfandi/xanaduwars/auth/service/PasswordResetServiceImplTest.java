package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Password;
import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWT;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.auth.value.PasswordResetToken;
import io.github.naomimyselfandi.xanaduwars.email.service.EmailService;
import io.github.naomimyselfandi.xanaduwars.email.value.ActivationContent;
import io.github.naomimyselfandi.xanaduwars.email.value.PasswordResetContent;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class PasswordResetServiceImplTest {

    @Mock
    private DecodedJWT decodedJWT;

    @Mock
    private AuthService authService;

    @Mock
    private JWTFactory jwtFactory;

    @Mock
    private JWTValidator validator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetServiceImpl fixture;

    @Test
    void createResetTokenForUserRequest(SeededRng random) {
        var account = random.<UserDetailsDto>get();
        var emailAddress = random.<EmailAddress>get();
        var jwt = random.<JWT>get();
        var token = new PasswordResetToken(jwt.token());
        when(authService.loadUserByEmailAddress(emailAddress)).thenReturn(Optional.of(account));
        when(jwtFactory.create(account, JWTPurpose.PASSWORD_RESET)).thenReturn(jwt);
        fixture.createResetTokenForUserRequest(emailAddress);
        verify(emailService).send(account.emailAddress(), new PasswordResetContent(account, token));
    }

    @Test
    void createResetTokenForNewAccount(SeededRng random) {
        var account = random.<UserDetailsDto>get();
        var jwt = random.<JWT>get();
        var token = new PasswordResetToken(jwt.token());
        when(jwtFactory.create(account, JWTPurpose.PASSWORD_RESET)).thenReturn(jwt);
        fixture.createResetTokenForNewAccount(account);
        verify(emailService).send(account.emailAddress(), new ActivationContent(account, token));
    }

    @Test
    void validateResetToken(SeededRng random) {
        var accountId = random.<Id<Account>>get();
        var account = random.<UserDetailsDto>get();
        var token = random.<PasswordResetToken>get();
        when(validator.validate(token.token(), JWTPurpose.PASSWORD_RESET)).thenReturn(Optional.of(decodedJWT));
        when(decodedJWT.getSubject()).thenReturn(accountId.toString());
        when(authService.loadUserById(accountId)).thenReturn(Optional.of(account));
        assertThat(fixture.validateResetToken(token)).contains(account);
    }

    @Test
    void resetPassword(SeededRng random) {
        var account = new Account();
        var accountId = random.<Id<Account>>get();
        var token = random.<PasswordResetToken>get();
        var plaintext = random.<Plaintext>get();
        var password = random.<Password>get();
        when(validator.validate(token.token(), JWTPurpose.PASSWORD_RESET)).thenReturn(Optional.of(decodedJWT));
        when(decodedJWT.getSubject()).thenReturn(accountId.toString());
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(passwordEncoder.encode(plaintext.text())).thenReturn(password.password());
        fixture.resetPassword(token, plaintext);
        assertThat(account.getPassword()).isEqualTo(password);
    }

}
