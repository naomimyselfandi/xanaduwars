package io.github.naomimyselfandi.xanaduwars.auth.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.auth.dto.AccessTokenResponse;
import io.github.naomimyselfandi.xanaduwars.auth.dto.LoginRequest;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.*;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTDurationService.RememberMe;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

import static io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTPurpose.*;
import static io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTClaim.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AuthControllerTest {

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    @Mock
    private DecodedJWT jwt;

    @Mock
    private HttpServletResponse response;

    @Mock
    private JWTFactory jwtFactory;

    @Mock
    private AuthService authService;

    @Mock
    private JWTValidator jwtValidator;

    @Mock
    private AccountService accountService;

    @Mock
    private JWTDurationService jwtDurationService;

    @InjectMocks
    private AuthController fixture;

    @EnumSource
    @ParameterizedTest
    void login(RememberMe rememberMe, SeededRng random) {
        var username = random.nextUsername();
        var password = random.nextPlaintextPassword();
        var dto = new UserDetailsDto();
        dto.setId(random.nextUUID());
        when(authService.find(username, password)).thenReturn(Optional.of(dto));
        var accessTokenDuration = Duration.ofMinutes(random.nextInt(5, 60));
        var accessToken = random.nextUUID().toString();
        when(jwtDurationService.duration(dto, ACCESS_TOKEN, rememberMe)).thenReturn(accessTokenDuration);
        when(jwtFactory.generateToken(dto, ACCESS_TOKEN, NONE, accessTokenDuration)).thenReturn(accessToken);
        var refreshTokenDuration = Duration.ofHours(random.nextInt(24, 576));
        var refreshToken = random.nextUUID().toString();
        when(jwtDurationService.duration(dto, REFRESH_TOKEN, rememberMe)).thenReturn(refreshTokenDuration);
        when(jwtFactory.generateToken(dto, REFRESH_TOKEN, NONE, refreshTokenDuration)).thenReturn(refreshToken);
        var request = new LoginRequest(username, password);
        assertThat(fixture.login(request, rememberMe, response))
                .isEqualTo(ResponseEntity.ok(new AccessTokenResponse(accessToken)));
        verify(response).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getValue()).satisfies(isRefreshCookie(refreshToken, refreshTokenDuration));
    }

    @EnumSource
    @ParameterizedTest
    void login_WhenTheCredentialsAreInvalid_ThenReturnsA401(RememberMe rememberMe, SeededRng random) {
        var username = random.nextUsername();
        var password = random.nextPlaintextPassword();
        when(authService.find(username, password)).thenReturn(Optional.empty());
        var request = new LoginRequest(username, password);
        assertThatThrownBy(() -> fixture.login(request, rememberMe, response))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("401 UNAUTHORIZED \"Invalid credentials.\"");
        verify(response, never()).addCookie(any());
    }

    @EnumSource
    @ParameterizedTest
    void refresh(RememberMe rememberMe, SeededRng random) {
        var originalToken = random.nextUUID().toString();
        when(jwtValidator.validateToken(originalToken, REFRESH_TOKEN, NONE)).thenReturn(Optional.of(jwt));
        var dto = new UserDetailsDto();
        dto.setId(random.nextUUID());
        when(jwt.getSubject()).thenReturn(dto.getId().toString());
        when(accountService.find(UserDetailsDto.class, dto.getId())).thenReturn(Optional.of(dto));
        var accessTokenDuration = Duration.ofMinutes(random.nextInt(5, 60));
        var accessToken = random.nextUUID().toString();
        when(jwtDurationService.duration(dto, ACCESS_TOKEN, rememberMe)).thenReturn(accessTokenDuration);
        when(jwtFactory.generateToken(dto, ACCESS_TOKEN, NONE, accessTokenDuration)).thenReturn(accessToken);
        var refreshTokenDuration = Duration.ofHours(random.nextInt(24, 576));
        var refreshToken = random.nextUUID().toString();
        when(jwtDurationService.duration(dto, REFRESH_TOKEN, rememberMe)).thenReturn(refreshTokenDuration);
        when(jwtFactory.generateToken(dto, REFRESH_TOKEN, NONE, refreshTokenDuration)).thenReturn(refreshToken);
        assertThat(fixture.refresh(originalToken, rememberMe, response))
                .isEqualTo(ResponseEntity.ok(new AccessTokenResponse(accessToken)));
        verify(response).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getValue()).satisfies(isRefreshCookie(refreshToken, refreshTokenDuration));
    }

    @EnumSource
    @ParameterizedTest
    void refresh_WhenTheRefreshTokenIsInvalid_ThenReturnsA401(RememberMe rememberMe, SeededRng random) {
        var refreshToken = random.nextUUID().toString();
        when(jwtValidator.validateToken(refreshToken, REFRESH_TOKEN, NONE)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.refresh(refreshToken, rememberMe, response))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("401 UNAUTHORIZED \"Invalid credentials.\"");
        verify(response, never()).addCookie(any());
    }

    @Test
    void logout() {
        assertThat(fixture.logout(response)).isEqualTo(ResponseEntity.noContent().build());
        verify(response).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getValue()).satisfies(isRefreshCookie("", Duration.ZERO));
    }

    private Consumer<Cookie> isRefreshCookie(String token, Duration duration) {
        return cookie -> {
            assertThat(cookie.getName()).isEqualTo("refresh_token");
            assertThat(cookie.getValue()).isEqualTo(token);
            assertThat(cookie.isHttpOnly()).isTrue();
            assertThat(cookie.getSecure()).isTrue();
            assertThat(cookie.getAttribute("SameSite")).isEqualTo("Strict");
            assertThat(cookie.getPath()).isEqualTo("/auth/refresh");
            assertThat(cookie.getMaxAge()).isEqualTo(duration.toSeconds());
        };
    }

}
