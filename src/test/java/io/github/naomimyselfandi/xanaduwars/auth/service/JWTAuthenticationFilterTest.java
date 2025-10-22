package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.account.value.RoleSet;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class JWTAuthenticationFilterTest {

    @Mock
    private DecodedJWT jwt;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JWTValidator jwtValidator;

    @Mock
    private AuthService authService;

    @Mock
    private Supplier<SecurityContext> securityContextSupplier;

    @InjectMocks
    private JWTAuthenticationFilter fixture;

    @BeforeEach
    void setup() {
        lenient().when(securityContextSupplier.get()).thenReturn(securityContext);
    }

    @AfterEach
    @SneakyThrows
    void assertDoFilterWasCalled() {
        verify(filterChain).doFilter(request, response);
    }

    @SneakyThrows
    @RepeatedTest(3)
    void doFilterInternal(SeededRng random) {
        var token = random.nextUUID().toString();
        var accountId = random.<Id<Account>>get();
        var user = random
                .<UserDetailsDto>get()
                .toBuilder()
                .roles(RoleSet.of(random.shuffle(Role.values()).subList(0, 2)))
                .build();
        when(authService.loadUserById(accountId)).thenReturn(Optional.of(user));
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtValidator.validate(token, JWTPurpose.ACCESS_TOKEN)).thenReturn(Optional.of(jwt));
        when(jwt.getSubject()).thenReturn(accountId.toString());
        fixture.doFilterInternal(request, response, filterChain);
        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        verify(securityContext).setAuthentication(authentication);
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenTheAuthorizationHeaderIsMissing_ThenDoesNothing() {
        when(request.getHeader("Authorization")).thenReturn(null);
        fixture.doFilterInternal(request, response, filterChain);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenNonBearerAuthenticationIsUsed_ThenDoesNothing() {
        when(request.getHeader("Authorization")).thenReturn("plz let me in, I am not hacker");
        fixture.doFilterInternal(request, response, filterChain);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenTheTokenIsInvalid_ThenDoesNothing(SeededRng random) {
        var token = random.nextUUID().toString();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtValidator.validate(token, JWTPurpose.ACCESS_TOKEN)).thenReturn(Optional.empty());
        fixture.doFilterInternal(request, response, filterChain);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    @SneakyThrows
    void doFilterInternal_WhenTheAccountDoesNotExist_ThenDoesNothing(SeededRng random) {
        var token = random.nextUUID().toString();
        var accountId = random.<Id<Account>>get();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtValidator.validate(token, JWTPurpose.ACCESS_TOKEN)).thenReturn(Optional.of(jwt));
        when(jwt.getSubject()).thenReturn(accountId.toString());
        when(authService.loadUserById(accountId)).thenReturn(Optional.empty());
        fixture.doFilterInternal(request, response, filterChain);
        verify(securityContext, never()).setAuthentication(any());
    }

}
