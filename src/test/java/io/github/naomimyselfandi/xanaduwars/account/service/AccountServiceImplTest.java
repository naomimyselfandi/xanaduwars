package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.entity.BotAccount;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.auth.service.HashService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AccountServiceImplTest {

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @Mock
    private HashService hashService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl fixture;

    @Test
    void find_ById(SeededRng random) {
        var id = random.nextUUID();
        var account = new HumanAccount();
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        assertThat(fixture.find(id)).contains(account);
    }

    @Test
    void find_ByUsername(SeededRng random) {
        var username = random.nextUsername();
        var account = new HumanAccount();
        when(accountRepository.findByCanonicalUsername(username.canonicalForm())).thenReturn(Optional.of(account));
        assertThat(fixture.find(username)).contains(account);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void find_ByUsernameAndPassword(boolean matches, SeededRng random) {
        var username = random.nextUsername();
        var password = random.nextPassword();
        var plaintext = random.nextPlaintextPassword();
        var account = new HumanAccount().authenticationSecret(password);
        when(accountRepository.findByCanonicalUsername(username.canonicalForm())).thenReturn(Optional.of(account));
        when(hashService.test(plaintext, password)).thenReturn(matches);
        if (matches) {
            assertThat(fixture.find(username, plaintext)).contains(account);
        } else {
            assertThat(fixture.find(username, plaintext)).isEmpty();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void find_ByUsernameAndAPIKey(boolean matches, SeededRng random) {
        var username = random.nextUsername();
        var apiKey = random.nextAPIKey();
        var plaintext = random.nextPlaintextAPIKey();
        var account = new BotAccount().authenticationSecret(apiKey);
        when(accountRepository.findByCanonicalUsername(username.canonicalForm())).thenReturn(Optional.of(account));
        when(hashService.test(plaintext, apiKey)).thenReturn(matches);
        if (matches) {
            assertThat(fixture.find(username, plaintext)).contains(account);
        } else {
            assertThat(fixture.find(username, plaintext)).isEmpty();
        }
    }

    @Test
    void find_ByEmailAddress(SeededRng random) {
        var emailAddress = random.nextEmailAddress();
        var account = new HumanAccount();
        when(accountRepository.findByEmailAddress(emailAddress)).thenReturn(Optional.of(account));
        assertThat(fixture.find(emailAddress)).contains(account);
    }

    @Test
    void create_Human(SeededRng random) {
        var saved = new HumanAccount();
        var username = random.nextUsername();
        var emailAddress = random.nextEmailAddress();
        var password = random.nextPassword();
        var plaintext = random.nextPlaintextPassword();
        when(hashService.hash(plaintext)).thenReturn(password);
        when(accountRepository.save(any())).thenReturn(saved);
        assertThat(fixture.create(username, emailAddress, plaintext)).contains(saved);
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
        var password = random.nextPlaintextPassword();
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
        var plaintext = random.nextPlaintextAPIKey();
        when(hashService.hash(plaintext)).thenReturn(apiKey);
        when(accountRepository.save(any())).thenReturn(saved);
        assertThat(fixture.create(username, emailAddress, plaintext)).contains(saved);
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
        var apiKey = random.nextPlaintextAPIKey();
        when(accountRepository.existsByCanonicalUsernameOrEmailAddress(username.canonicalForm(), emailAddress))
                .thenReturn(true);
        assertThat(fixture.create(username, emailAddress, apiKey)).isEmpty();
        verify(accountRepository, never()).save(any());
    }

}
