package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.entity.BotAccount;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.account.value.*;
import io.github.naomimyselfandi.xanaduwars.auth.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
class AccountServiceImpl implements AccountService {

    private final HashService hashService;
    private final AccountRepository accountRepository;

    @Override
    public Optional<Account> find(UUID id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> find(Username username) {
        return accountRepository.findByCanonicalUsername(username.canonicalForm());
    }

    @Override
    public Optional<Account> find(Username username, Plaintext<?> secret) {
        return find(username).filter(account -> hashService.test(secret, account.authenticationSecret()));
    }

    @Override
    public Optional<Account> find(EmailAddress emailAddress) {
        return accountRepository.findByEmailAddress(emailAddress);
    }

    @Override
    public Optional<Account> create(Username username, EmailAddress emailAddress, Plaintext<?> secret) {
        if (accountRepository.existsByCanonicalUsernameOrEmailAddress(username.canonicalForm(), emailAddress)) {
            return Optional.empty();
        } else {
            var hash = hashService.hash(secret);
            var account = (switch (hash) {
                case APIKey apiKey -> new BotAccount().authenticationSecret(apiKey);
                case Password password -> new HumanAccount().authenticationSecret(password);
            }).username(username).emailAddress(emailAddress);
            return Optional.of(accountRepository.save(account));
        }
    }

}
