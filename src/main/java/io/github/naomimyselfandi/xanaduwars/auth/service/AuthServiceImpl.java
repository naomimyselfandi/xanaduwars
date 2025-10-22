package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService {

    private final Supplier<SecurityContext> securityContextSupplier;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTValidator jwtValidator;

    @Override
    public Optional<UserDetailsDto> loadForAuthenticatedUser() {
        return Optional
                .ofNullable(securityContextSupplier.get().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(principal -> principal instanceof UserDetailsDto dto ? dto : null);
    }

    @Override
    public Optional<UserDetailsDto> loadUserById(Id<Account> id) {
        return accountRepository.findById(id, UserDetailsDto.class);
    }

    @Override
    public Optional<UserDetailsDto> loadUserByName(Username username) {
        return accountRepository.findByCanonicalUsername(username.toCanonicalForm(), UserDetailsDto.class);
    }

    @Override
    public Optional<UserDetailsDto> loadUserByEmailAddress(EmailAddress emailAddress) {
        return accountRepository.findByEmailAddress(emailAddress, UserDetailsDto.class);
    }

    @Override
    public Optional<UserDetailsDto> loadUserByCredentials(Username username, Plaintext password) {
        return loadUserByName(username)
                .filter(it -> passwordEncoder.matches(password.text(), it.getPassword()));
    }

    @Override
    public Optional<UserDetailsDto> loadUserByRefreshToken(String refreshToken) {
        return jwtValidator
                .validate(refreshToken, JWTPurpose.REFRESH_TOKEN)
                .map(DecodedJWT::getSubject)
                .map(UUID::fromString)
                .map(Id<Account>::new)
                .flatMap(this::loadUserById);
    }

    @Override
    public void setRememberMe(Id<Account> accountId, boolean rememberMe) {
        accountRepository.updateRememberMeById(accountId, rememberMe);
    }

}
