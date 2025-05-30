package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.BotAccount;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class APIKeyAuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private AccountService accountService;

    @Mock
    private HashService hashService;

    @InjectMocks
    private APIKeyAuthenticationFilter fixture;

    private int proceed = 1;

    @BeforeEach
    void setup() {
        clearSecurityContext();
    }

    @AfterAll
    static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    @SneakyThrows
    void assertDoFilterWasCalled() {
        verify(filterChain, times(proceed)).doFilter(request, response);
    }

    @SneakyThrows
    @RepeatedTest(3)
    void doFilterInternal(SeededRng random) {
        var plaintext = random.nextPlaintextAPIKey();
        var apiKey = random.nextAPIKey();
        var accountId = random.nextUUID();
        var basic = Base64
                .getEncoder()
                .encodeToString("%s:%s".formatted(accountId, plaintext.text()).getBytes());
        var account = new BotAccount()
                .authenticationSecret(apiKey)
                .setRole(random.pick(Role.values()), true);
        var principal = new UserDetailsImpl(account);
        when(request.getHeader("Authorization")).thenReturn("Basic " + basic);
        when(hashService.test(plaintext, apiKey)).thenReturn(true);
        when(accountService.find(accountId)).thenReturn(Optional.of(account));
        fixture.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .returns(principal, Authentication::getPrincipal)
                .returns(null, Authentication::getCredentials)
                .returns(true, Authentication::isAuthenticated)
                .extracting(Authentication::getAuthorities)
                .asInstanceOf(InstanceOfAssertFactories.COLLECTION)
                .containsOnlyOnceElementsOf(principal.getAuthorities());
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenTheAuthorizationHeaderIsMissing_ThenDoesNothing() {
        when(request.getHeader("Authorization")).thenReturn(null);
        fixture.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenNonBasicAuthenticationIsUsed_ThenDoesNothing() {
        when(request.getHeader("Authorization")).thenReturn("plz let me in, I am not hacker");
        fixture.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenTheTokenIsInvalid_ThenDoesNothing(SeededRng random) {
        var token = random.nextUUID().toString();
        when(request.getHeader("Authorization")).thenReturn("Basic " + token);
        fixture.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenNoAPIKeyIsGiven_ThenDoesNothing(SeededRng random) {
        var accountId = random.nextUUID();
        var basic = Base64.getEncoder().encodeToString("%s".formatted(accountId).getBytes());
        when(request.getHeader("Authorization")).thenReturn("Basic " + basic);
        fixture.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenTheAccountDoesNotExist_ThenDoesNothing(SeededRng random) {
        var accountId = random.nextUUID();
        var basic = Base64
                .getEncoder()
                .encodeToString("%s:xyz".formatted(accountId).getBytes());
        when(request.getHeader("Authorization")).thenReturn("Basic " + basic);
        when(accountService.find(accountId)).thenReturn(Optional.empty());
        fixture.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenTheAPIKeyDoesNotMatch_ThenDoesNothing(SeededRng random) {
        var plaintext = random.nextPlaintextAPIKey();
        var apiKey = random.nextAPIKey();
        var accountId = random.nextUUID();
        var basic = Base64
                .getEncoder()
                .encodeToString("%s:%s".formatted(accountId, plaintext.text()).getBytes());
        var account = new BotAccount().authenticationSecret(apiKey);
        when(request.getHeader("Authorization")).thenReturn("Basic " + basic);
        when(hashService.test(plaintext, apiKey)).thenReturn(false);
        when(accountService.find(accountId)).thenReturn(Optional.of(account));
        fixture.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenTheAccountIsNotABot_ThenDoesNothing(SeededRng random) {
        var plaintext = random.nextPlaintextAPIKey();
        var password = random.nextPassword();
        var accountId = random.nextUUID();
        var basic = Base64
                .getEncoder()
                .encodeToString("%s:%s".formatted(accountId, plaintext.text()).getBytes());
        var account = new HumanAccount().authenticationSecret(password);
        when(request.getHeader("Authorization")).thenReturn("Basic " + basic);
        when(hashService.test(plaintext, password)).thenReturn(true);
        when(accountService.find(accountId)).thenReturn(Optional.of(account));
        fixture.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldNotFilter(boolean expected) {
        proceed = 0;
        if (expected) SecurityContextHolder.getContext().setAuthentication(mock());
        assertThat(fixture.shouldNotFilter(request)).isEqualTo(expected);
    }

}
