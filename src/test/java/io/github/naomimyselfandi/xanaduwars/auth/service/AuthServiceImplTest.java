package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.Password;
import io.github.naomimyselfandi.xanaduwars.account.value.PlaintextPassword;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AuthServiceImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl fixture;

    @BeforeEach
    void setup() {
        clearSecurityContext();
    }

    @AfterAll
    static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tryGet(SeededRng random) {
        var principal = random.<UserDetailsDto>get();
        var authentication = new UsernamePasswordAuthenticationToken(principal, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThat(fixture.tryGet()).contains(principal);
    }

    @Test
    void tryGet_WhenTheSecurityContextIsEmpty_ThenReturnsAnEmptyResult() {
        assertThat(fixture.tryGet()).isEmpty();
    }

    @Test
    void tryGet_WhenTheSecurityContextHoldsAnUnexpectedPrincipal_ThenReturnsAnEmptyResult() {
        var principal = "please let me in, I am not hacker";
        var authentication = new UsernamePasswordAuthenticationToken(principal, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThat(fixture.tryGet()).isEmpty();
    }

    @Test
    void get(SeededRng random) {
        var principal = random.<UserDetailsDto>get();
        var authentication = new UsernamePasswordAuthenticationToken(principal, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThat(fixture.get()).isEqualTo(principal);
    }

    @Test
    void get_WhenTheSecurityContextIsEmpty_ThenThrows() {
        assertThatThrownBy(fixture::get).isInstanceOfSatisfying(
                ResponseStatusException.class,
                it -> assertThat(it.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(401))
        );
    }

    @Test
    void getId(SeededRng random) {
        var principal = random.<UserDetailsDto>get();
        var authentication = new UsernamePasswordAuthenticationToken(principal, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThat(fixture.getId()).isEqualTo(principal.getId());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void find(boolean matches, SeededRng random) throws NoSuchEntityException {
        var username = random.<Username>get();
        var password = random.<Password>get();
        var plaintext = random.<PlaintextPassword>get();
        var dto = new UserDetailsDto();
        dto.setPassword(password.text());
        when(accountService.get(UserDetailsDto.class, username)).thenReturn(dto);
        when(passwordEncoder.matches(plaintext.text(), password.text())).thenReturn(matches);
        if (matches) {
            assertThat(fixture.find(username, plaintext)).contains(dto);
        } else {
            assertThat(fixture.find(username, plaintext)).isEmpty();
        }
    }

    @Test
    void find_WhenTheUserDoesNotExist_ThenEmpty(SeededRng random) throws NoSuchEntityException {
        var username = random.<Username>get();
        var plaintext = random.<PlaintextPassword>get();
        when(accountService.get(UserDetailsDto.class, username)).thenThrow(NoSuchEntityException.class);
        assertThat(fixture.find(username, plaintext)).isEmpty();
    }

}
