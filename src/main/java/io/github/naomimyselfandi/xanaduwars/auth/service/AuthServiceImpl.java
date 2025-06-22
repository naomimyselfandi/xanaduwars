package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.PlaintextPassword;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.function.Supplier;

@Service("authService")
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService {

    private static final Supplier<ResponseStatusException> UNAUTHORIZED = () ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<UserDetailsDto> tryGet() {
        return Optional
                .ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(principal -> principal instanceof UserDetailsDto dto ? dto : null);
    }

    @Override
    public UserDetailsDto get() {
        return tryGet().orElseThrow(UNAUTHORIZED);
    }

    @Override
    public Id<Account> getId() {
        return get().getId();
    }

    @Override
    public Optional<UserDetailsDto> find(Username username, PlaintextPassword secret) {
        try {
            return Optional.of(accountService.get(UserDetailsDto.class, username))
                    .filter(dto -> passwordEncoder.matches(secret.text(), dto.getPassword()));
        } catch (NoSuchEntityException _) {
            return Optional.empty();
        }
    }

}
