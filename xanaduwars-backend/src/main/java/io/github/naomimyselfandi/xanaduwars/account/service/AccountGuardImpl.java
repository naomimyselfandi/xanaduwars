package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.value.AccountId;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountReference;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("accountGuard")
@RequiredArgsConstructor
class AccountGuardImpl implements AccountGuard {

    private final AuthService authService;

    @Override
    public boolean isCurrentAccount(AccountReference reference) {
        return switch (reference) {
            case AccountReference.Me _ -> true;
            case AccountId id -> authService.tryGet().map(UserDetailsDto::getId).filter(id::equals).isPresent();
        };
    }

}
