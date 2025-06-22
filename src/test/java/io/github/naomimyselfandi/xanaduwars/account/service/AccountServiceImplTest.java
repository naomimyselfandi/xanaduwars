package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.dto.AccountSettingsDto;
import io.github.naomimyselfandi.xanaduwars.account.dto.FullAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountSettings;
import io.github.naomimyselfandi.xanaduwars.account.value.*;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AccountServiceImplTest {

    private interface Helper extends AccountDto {}

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @Mock
    private Helper dto;

    @Mock
    private Clock clock;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private AccountServiceImpl fixture;

    @Test
    void get_ById(SeededRng random) throws NoSuchEntityException {
        var id = random.<Id<Account>>get();
        var account = new Account();
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(conversionService.convert(account, Helper.class)).thenReturn(dto);
        assertThat(fixture.get(Helper.class, id)).isEqualTo(dto);
    }

    @Test
    void get_ById_WhenTheAccountDoesNotExist_ThenThrows(SeededRng random) {
        var id = random.<Id<Account>>get();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.get(Helper.class, id)).isInstanceOf(NoSuchEntityException.class);
    }

    @Test
    void get_ByUsername(SeededRng random) throws NoSuchEntityException {
        var username = random.<Username>get();
        var account = new Account();
        when(accountRepository.findByCanonicalUsername(username.toCanonicalForm())).thenReturn(Optional.of(account));
        when(conversionService.convert(account, Helper.class)).thenReturn(dto);
        assertThat(fixture.get(Helper.class, username)).isEqualTo(dto);
    }

    @Test
    void get_ByUsername_WhenTheAccountDoesNotExist_ThenThrows(SeededRng random) {
        var username = random.<Username>get();
        when(accountRepository.findByCanonicalUsername(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.get(Helper.class, username)).isInstanceOf(NoSuchEntityException.class);
    }

    @Test
    void get_ByEmailAddress(SeededRng random) throws NoSuchEntityException {
        var emailAddress = random.<EmailAddress>get();
        var account = new Account();
        when(accountRepository.findByEmailAddress(emailAddress)).thenReturn(Optional.of(account));
        when(conversionService.convert(account, Helper.class)).thenReturn(dto);
        assertThat(fixture.get(Helper.class, emailAddress)).isEqualTo(dto);
    }

    @Test
    void get_ByEmailAddress_WhenTheAccountDoesNotExist_ThenThrows(SeededRng random) {
        var emailAddress = random.<EmailAddress>get();
        when(accountRepository.findByEmailAddress(emailAddress)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.get(Helper.class, emailAddress)).isInstanceOf(NoSuchEntityException.class);
    }

    @Test
    void getForAuthentication(SeededRng random) throws NoSuchEntityException {
        var id = random.<Id<Account>>get();
        var dto = random.<UserDetailsDto>get();
        var now = random.<Instant>get();
        var account = new Account();
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(conversionService.convert(account, UserDetailsDto.class)).thenReturn(dto);
        when(clock.instant()).thenReturn(now);
        assertThat(fixture.getForAuthentication(id)).isEqualTo(dto);
        assertThat(account.getLastSeenAt()).isEqualTo(now);
    }

    @EnumSource
    @ParameterizedTest
    void create(Role role, SeededRng random) {
        var saved = new Account();
        var username = random.<Username>get();
        var emailAddress = random.<EmailAddress>get();
        var password = random.<Password>get();
        when(accountRepository.save(any())).thenReturn(saved);
        fixture.create(username, emailAddress, password, role);
        verify(accountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue()).satisfies(account -> {
            assertThat(account.getUsername()).isEqualTo(username);
            assertThat(account.getEmailAddress()).isEqualTo(emailAddress);
            assertThat(account.getPassword()).isEqualTo(password);
            assertThat(account.hasRole(role)).isTrue();
        });
    }

    @Test
    void updateSettings(SeededRng random) throws NoSuchEntityException {
        var id = random.<Id<Account>>get();
        var account = random.<Account>get();
        var dto = random.<AccountSettingsDto>get();
        var settings = random.<AccountSettings>get();
        var out = random.<FullAccountDto>get();
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(conversionService.convert(dto, AccountSettings.class)).thenReturn(settings);
        when(conversionService.convert(account, FullAccountDto.class)).then(_ -> {
            assertThat(account.getSettings()).isEqualTo(settings);
            return out;
        });
        assertThat(fixture.updateSettings(id, dto)).isEqualTo(out);
    }

    @Test
    void create_WhenTheUsernameOrEmailAddressIsInUse_ThenDoesNothing(SeededRng random) {
        var username = random.<Username>get();
        var emailAddress = random.<EmailAddress>get();
        var password = random.<Password>get();
        when(accountRepository.existsByCanonicalUsernameOrEmailAddress(username.toCanonicalForm(), emailAddress))
                .thenReturn(true);
        fixture.create(username, emailAddress, password);
        verify(accountRepository, never()).save(any());
    }

}
