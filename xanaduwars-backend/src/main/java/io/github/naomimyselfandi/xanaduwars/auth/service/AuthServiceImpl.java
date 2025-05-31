package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService {

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
    public Optional<UserDetailsDto> find(Username username, Plaintext<?> secret) {
        return accountService
                .find(UserDetailsDto.class, username)
                .filter(dto -> passwordEncoder.matches(secret.text(), dto.getPassword()));
    }

}
