package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTClaim;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTValidator;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class JWTAuthenticationFilterTest {

    @Mock
    private DecodedJWT jwt;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JWTValidator jwtValidator;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private JWTAuthenticationFilter fixture;

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
        var token = random.nextUUID().toString();
        var accountId = random.nextUUID();
        var account = new HumanAccount().setRole(random.pick(Role.values()), true);
        var principal = new UserDetailsImpl(account);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtValidator.validateToken(token, JWTPurpose.ACCESS_TOKEN, JWTClaim.NONE)).thenReturn(Optional.of(jwt));
        when(jwt.getSubject()).thenReturn(accountId.toString());
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
    void doFilterInternal_WhenNonBearerAuthenticationIsUsed_ThenDoesNothing() {
        when(request.getHeader("Authorization")).thenReturn("plz let me in, I am not hacker");
        fixture.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenTheTokenIsInvalid_ThenDoesNothing(SeededRng random) {
        var token = random.nextUUID().toString();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtValidator.validateToken(token, JWTPurpose.ACCESS_TOKEN, JWTClaim.NONE)).thenReturn(Optional.empty());
        fixture.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenTheAccountDoesNotExist_ThenDoesNothing(SeededRng random) {
        var token = random.nextUUID().toString();
        var accountId = random.nextUUID();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtValidator.validateToken(token, JWTPurpose.ACCESS_TOKEN, JWTClaim.NONE)).thenReturn(Optional.of(jwt));
        when(jwt.getSubject()).thenReturn(accountId.toString());
        when(accountService.find(accountId)).thenReturn(Optional.empty());
        fixture.doFilterInternal(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            /auth/foo,true
            /info/bar,false
            /game/new,false
            """)
    void shouldNotFilter(String path, boolean expected) {
        proceed = 0;
        when(request.getRequestURI()).thenReturn(path);
        assertThat(fixture.shouldNotFilter(request)).isEqualTo(expected);
    }

}
