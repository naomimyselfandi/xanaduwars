package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class CurrentAccountServiceImpl implements CurrentAccountService {

    @Override
    public Optional<Account> tryGet() {
        return Optional
                .ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(principal -> principal instanceof UserDetailsImpl(var account) ? account : null);
    }

    @Override
    public Account get() {
        return tryGet().orElseThrow(() -> new AccessDeniedException("Not logged in."));
    }

}
