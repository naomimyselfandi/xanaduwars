package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.value.AccountIdReference;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountReference;
import io.github.naomimyselfandi.xanaduwars.account.value.CurrentAccountReference;
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
            case CurrentAccountReference _ -> true;
            case AccountIdReference(var id) -> {
                var currentId = authService.tryGet().map(UserDetailsDto::getId).orElse(null);
                yield id.equals(currentId);
            }
        };
    }

}
