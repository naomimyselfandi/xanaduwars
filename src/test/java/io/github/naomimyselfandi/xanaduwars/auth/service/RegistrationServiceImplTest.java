package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.value.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RegistrationServiceImplTest {

    @Captor
    private ArgumentCaptor<Account> captor;

    @Mock
    private Clock clock;

    @Mock
    private Supplier<UUID> uuidSupplier;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private RegistrationServiceImpl fixture;

    @Test
    void register(SeededRng random) {
        when(accountRepository.count()).thenReturn((long) random.nextInt(1, Integer.MAX_VALUE));
        var username = random.<Username>get();
        var emailAddress = random.<EmailAddress>get();
        var now = random.nextInstant();
        var uuid = random.nextUUID();
        var password = random.<Password>get();
        when(clock.instant()).thenReturn(now).thenThrow(AssertionError.class);
        when(uuidSupplier.get()).thenReturn(uuid).thenThrow(AssertionError.class);
        when(passwordEncoder.encode(uuid.toString())).thenReturn(password.password());
        fixture.register(username, emailAddress);
        verify(accountRepository).save(captor.capture());
        var account = captor.getValue();
        assertThat(account)
                .returns(username, Account::getUsername)
                .returns(emailAddress, Account::getEmailAddress)
                .returns(password, Account::getPassword)
                .returns(RoleSet.NONE, Account::getRoles)
                .returns(now, Account::getCreatedAt)
                .returns(Instant.EPOCH, Account::getLastSeenAt)
                .returns(false, Account::isRememberMe);
        verify(passwordResetService).createResetTokenForNewAccount(new UserDetailsDto(
                account.getId(),
                username,
                emailAddress,
                password,
                RoleSet.NONE,
                false
        ));
    }

    @Test
    void register_WhenTheEmailAddressIsTheRootEmailAddress_ThenMakesTheUserAnAdmin(SeededRng random) {
        var username = random.<Username>get();
        var emailAddress = random.<EmailAddress>get();
        var now = random.nextInstant();
        var uuid = random.nextUUID();
        var password = random.<Password>get();
        when(clock.instant()).thenReturn(now).thenThrow(AssertionError.class);
        when(uuidSupplier.get()).thenReturn(uuid).thenThrow(AssertionError.class);
        when(passwordEncoder.encode(uuid.toString())).thenReturn(password.password());
        fixture.register(username, emailAddress);
        verify(accountRepository).save(captor.capture());
        var account = captor.getValue();
        assertThat(account)
                .returns(username, Account::getUsername)
                .returns(emailAddress, Account::getEmailAddress)
                .returns(password, Account::getPassword)
                .returns(RoleSet.of(List.of(Role.ADMIN)), Account::getRoles)
                .returns(now, Account::getCreatedAt)
                .returns(Instant.EPOCH, Account::getLastSeenAt)
                .returns(false, Account::isRememberMe);
        verify(passwordResetService).createResetTokenForNewAccount(new UserDetailsDto(
                account.getId(),
                username,
                emailAddress,
                password,
                RoleSet.of(List.of(Role.ADMIN)),
                false
        ));
    }

    @Test
    void register_WhenTheUsernameOrEmailAddressIsInUse_ThenDoesNothing(SeededRng random) {
        var username = random.<Username>get();
        var emailAddress = random.<EmailAddress>get();
        when(accountRepository.existsByCanonicalUsernameOrEmailAddress(username.toCanonicalForm(), emailAddress))
                .thenReturn(true);
        assertThatCode(() -> fixture.register(username, emailAddress)).doesNotThrowAnyException();
        verify(accountRepository, never()).save(any());
        verify(passwordResetService, never()).createResetTokenForNewAccount(any());
    }

}
