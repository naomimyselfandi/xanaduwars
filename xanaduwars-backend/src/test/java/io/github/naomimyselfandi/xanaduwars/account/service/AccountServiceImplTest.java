package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.entity.BotAccount;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

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
    private AccountRepository accountRepository;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private AccountServiceImpl fixture;

    @Test
    void find_ById(SeededRng random) {
        var id = random.nextUUID();
        var account = new HumanAccount();
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(conversionService.convert(account, Helper.class)).thenReturn(dto);
        assertThat(fixture.find(Helper.class, id)).contains(dto);
    }

    @Test
    void find_ByUsername(SeededRng random) {
        var username = random.nextUsername();
        var account = new HumanAccount();
        when(accountRepository.findByCanonicalUsername(username.canonicalForm())).thenReturn(Optional.of(account));
        when(conversionService.convert(account, Helper.class)).thenReturn(dto);
        assertThat(fixture.find(Helper.class, username)).contains(dto);
    }

    @Test
    void find_ByEmailAddress(SeededRng random) {
        var emailAddress = random.nextEmailAddress();
        var account = new HumanAccount();
        when(accountRepository.findByEmailAddress(emailAddress)).thenReturn(Optional.of(account));
        when(conversionService.convert(account, Helper.class)).thenReturn(dto);
        assertThat(fixture.find(Helper.class, emailAddress)).contains(dto);
    }

    @Test
    void create_Human(SeededRng random) {
        var saved = new HumanAccount();
        var username = random.nextUsername();
        var emailAddress = random.nextEmailAddress();
        var password = random.nextPassword();
        when(accountRepository.save(any())).thenReturn(saved);
        assertThat(fixture.create(username, emailAddress, password)).contains(saved);
        verify(accountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue()).satisfies(account -> {
            assertThat(account).isInstanceOf(HumanAccount.class);
            assertThat(account.username()).isEqualTo(username);
            assertThat(account.emailAddress()).isEqualTo(emailAddress);
            assertThat(account.authenticationSecret()).isEqualTo(password);
        });
    }

    @Test
    void create_Human_WhenTheUsernameOrEmailAddressIsInUse_ThenDoesNothing(SeededRng random) {
        var username = random.nextUsername();
        var emailAddress = random.nextEmailAddress();
        var password = random.nextPassword();
        when(accountRepository.existsByCanonicalUsernameOrEmailAddress(username.canonicalForm(), emailAddress))
                .thenReturn(true);
        assertThat(fixture.create(username, emailAddress, password)).isEmpty();
        verify(accountRepository, never()).save(any());
    }

    @Test
    void create_Bot(SeededRng random) {
        var saved = new BotAccount();
        var username = random.nextUsername();
        var emailAddress = random.nextEmailAddress();
        var apiKey = random.nextAPIKey();
        when(accountRepository.save(any())).thenReturn(saved);
        assertThat(fixture.create(username, emailAddress, apiKey)).contains(saved);
        verify(accountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue()).satisfies(account -> {
            assertThat(account).isInstanceOf(BotAccount.class);
            assertThat(account.username()).isEqualTo(username);
            assertThat(account.emailAddress()).isEqualTo(emailAddress);
            assertThat(account.authenticationSecret()).isEqualTo(apiKey);
        });
    }

    @Test
    void create_Bot_WhenTheUsernameOrEmailAddressIsInUse_ThenDoesNothing(SeededRng random) {
        var username = random.nextUsername();
        var emailAddress = random.nextEmailAddress();
        var apiKey = random.nextAPIKey();
        when(accountRepository.existsByCanonicalUsernameOrEmailAddress(username.canonicalForm(), emailAddress))
                .thenReturn(true);
        assertThat(fixture.create(username, emailAddress, apiKey)).isEmpty();
        verify(accountRepository, never()).save(any());
    }

}
