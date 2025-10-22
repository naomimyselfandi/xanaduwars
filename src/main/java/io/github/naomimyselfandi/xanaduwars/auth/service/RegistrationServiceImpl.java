package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.value.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
class RegistrationServiceImpl implements RegistrationService {

    private final Clock clock;
    private final Supplier<UUID> uuidSupplier;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final PasswordResetService passwordResetService;

    @Override
    @Transactional
    public void register(Username username, EmailAddress emailAddress) {
        if (accountRepository.existsByCanonicalUsernameOrEmailAddress(username.toCanonicalForm(), emailAddress)) return;
        var account = getAccount(username, emailAddress);
        accountRepository.save(account);
        sendRegistrationEmail(account);
    }

    private Account getAccount(Username username, EmailAddress emailAddress) {
        return new Account()
                .setUsername(username)
                .setEmailAddress(emailAddress)
                .setPassword(getRandomPassword())
                .setRoles(getRoles())
                .setCreatedAt(clock.instant());
    }

    private Password getRandomPassword() {
        var plaintext = uuidSupplier.get().toString();
        var encoded = passwordEncoder.encode(plaintext);
        return new Password(encoded);
    }

    private RoleSet getRoles() {
        return accountRepository.count() == 0 ? RoleSet.of(Set.of(Role.ADMIN)) : RoleSet.NONE;
    }

    private void sendRegistrationEmail(Account account) {
        passwordResetService.createResetTokenForNewAccount(new UserDetailsDto(
                account.getId(),
                account.getUsername(),
                account.getEmailAddress(),
                account.getPassword(),
                account.getRoles(),
                account.isRememberMe()
        ));
    }

}
