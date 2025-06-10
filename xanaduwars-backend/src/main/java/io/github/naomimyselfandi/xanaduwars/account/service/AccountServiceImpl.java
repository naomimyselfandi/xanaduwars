package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.entity.BotAccount;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.account.value.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ConversionService conversionService;

    // The Account entity is currently pretty small, so loading it directly and
    // letting ConversionService handle the conversion is fine. If it grows, we
    // may want to refactor this to only select the fields in the DTO, or write
    // specializations for certain DTOs, but we're not worrying about that yet.

    @Override
    @Transactional(readOnly = true)
    public <T extends AccountDto> Optional<T> find(Class<T> dto, AccountId id) {
        return accountRepository.findById(id).map(as(dto));
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends AccountDto> Optional<T> find(Class<T> dto, Username username) {
        return accountRepository.findByCanonicalUsername(username.toCanonicalForm()).map(as(dto));
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends AccountDto> Optional<T> find(Class<T> dto, EmailAddress emailAddress) {
        return accountRepository.findByEmailAddress(emailAddress).map(as(dto));
    }

    @Override
    @Transactional
    public Optional<Account> create(Username username, EmailAddress emailAddress, Hash secret) {
        if (accountRepository.existsByCanonicalUsernameOrEmailAddress(username.toCanonicalForm(), emailAddress)) {
            return Optional.empty();
        } else {
            var account = (switch (secret) {
                case APIKey apiKey -> new BotAccount().setAuthenticationSecret(apiKey);
                case Password password -> new HumanAccount().setAuthenticationSecret(password);
            }).setUsername(username).setEmailAddress(emailAddress);
            return Optional.of(accountRepository.save(account));
        }
    }

    private <T> Function<Account, T> as(Class<T> dto) {
        return account -> conversionService.convert(account, dto);
    }

}
