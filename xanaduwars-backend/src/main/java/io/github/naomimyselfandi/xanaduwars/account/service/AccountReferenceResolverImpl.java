package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountId;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountReference;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class AccountReferenceResolverImpl implements AccountReferenceResolver {

    private final AuthService authService;
    private final AccountService accountService;

    @Override
    public <T extends AccountDto> Optional<T> resolve(Class<T> dto, AccountReference accountReference) {
        return switch (accountReference) {
            case AccountId id -> accountService.find(dto, id);
            case AccountReference.Me() when dto == UserDetailsDto.class -> authService
                    .tryGet()
                    .map(dto::cast);
            case AccountReference.Me() -> authService
                    .tryGet()
                    .map(UserDetailsDto::getId)
                    .flatMap(id -> accountService.find(dto, id));
        };
    }


}
