package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Password;
import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.auth.value.PasswordResetToken;
import io.github.naomimyselfandi.xanaduwars.email.service.EmailService;
import io.github.naomimyselfandi.xanaduwars.email.value.ActivationContent;
import io.github.naomimyselfandi.xanaduwars.email.value.PasswordResetContent;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
class PasswordResetServiceImpl implements PasswordResetService {

    private final AuthService authService;
    private final JWTFactory jwtFactory;
    private final JWTValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final EmailService emailService;

    @Override
    public void createResetTokenForUserRequest(EmailAddress emailAddress) {
        authService
                .loadUserByEmailAddress(emailAddress)
                .ifPresent(account -> {
                    var jwt = jwtFactory.create(account, JWTPurpose.PASSWORD_RESET);
                    var token = new PasswordResetToken(jwt.token());
                    emailService.send(account.emailAddress(), new PasswordResetContent(account, token));
                });
    }

    @Override
    public void createResetTokenForNewAccount(UserDetailsDto account) {
        var jwt = jwtFactory.create(account, JWTPurpose.PASSWORD_RESET);
        var token = new PasswordResetToken(jwt.token());
        emailService.send(account.emailAddress(), new ActivationContent(account, token));
    }

    @Override
    public Optional<UserDetailsDto> validateResetToken(PasswordResetToken token) {
        return getAccountId(token).flatMap(authService::loadUserById);
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetToken token, @Valid Plaintext password) {
        getAccountId(token)
                .flatMap(accountRepository::findById)
                .ifPresent(it -> it.setPassword(new Password(passwordEncoder.encode(password.text()))));
    }

    private Optional<Id<Account>> getAccountId(PasswordResetToken token) {
        return validator
                .validate(token.token(), JWTPurpose.PASSWORD_RESET)
                .map(DecodedJWT::getSubject)
                .map(UUID::fromString)
                .map(Id<Account>::new);
    }

}
