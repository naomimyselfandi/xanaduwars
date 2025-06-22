package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.dto.AccountSettingsDto;
import io.github.naomimyselfandi.xanaduwars.account.dto.FullAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountSettings;
import io.github.naomimyselfandi.xanaduwars.account.value.*;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
class AccountServiceImpl implements AccountService {

    private final Clock clock;
    private final AccountRepository accountRepository;
    private final ConversionService conversionService;

    // The Account entity is currently pretty small, so loading it directly and
    // letting ConversionService handle the conversion is fine. If it grows, we
    // may want to refactor this to only select the fields in the DTO, or write
    // specializations for certain DTOs, but we're not worrying about that yet.

    @Override
    @Transactional(readOnly = true)
    public <T extends AccountDto> T get(Class<T> dto, @Nullable Id<Account> id) throws NoSuchEntityException {
        return Optional
                .ofNullable(id)
                .flatMap(accountRepository::findById)
                .map(as(dto))
                .orElseThrow(NoSuchEntityException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends AccountDto> T get(Class<T> dto, Username username) throws NoSuchEntityException {
        return accountRepository
                .findByCanonicalUsername(username.toCanonicalForm())
                .map(as(dto))
                .orElseThrow(NoSuchEntityException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends AccountDto> T get(Class<T> dto, EmailAddress emailAddress) throws NoSuchEntityException {
        return accountRepository
                .findByEmailAddress(emailAddress)
                .map(as(dto))
                .orElseThrow(NoSuchEntityException::new);
    }

    @Override
    @Transactional
    public UserDetailsDto getForAuthentication(@Nullable Id<Account> id) throws NoSuchEntityException {
        var account = load(id);
        account.setLastSeenAt(clock.instant());
        return Objects.requireNonNull(conversionService.convert(account, UserDetailsDto.class));
    }

    @Override
    @Transactional
    public void create(Username username, EmailAddress emailAddress, Password password, Role... roles) {
        if (!accountRepository.existsByCanonicalUsernameOrEmailAddress(username.toCanonicalForm(), emailAddress)) {
            var account = new Account()
                    .setUsername(username)
                    .setEmailAddress(emailAddress)
                    .setPassword(password);
            for (var role : roles) account.setRole(role, true);
            accountRepository.save(account);
            // Holding off on email verification for now.
        }
    }

    @Override
    @Transactional
    public FullAccountDto updateSettings(Id<Account> id, AccountSettingsDto settings) throws NoSuchEntityException {
        var account = load(id);
        account.setSettings(Objects.requireNonNull(conversionService.convert(settings, AccountSettings.class)));
        return Objects.requireNonNull(conversionService.convert(account, FullAccountDto.class));
    }

    private Account load(@Nullable Id<Account> id) throws NoSuchEntityException {
        return Optional
                .ofNullable(id)
                .flatMap(accountRepository::findById)
                .orElseThrow(NoSuchEntityException::new);
    }

    private <T> Function<Account, T> as(Class<T> dto) {
        return account -> conversionService.convert(account, dto);
    }

}
