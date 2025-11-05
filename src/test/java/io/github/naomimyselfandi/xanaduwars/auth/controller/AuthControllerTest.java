package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.auth.service.JWTFactory;
import io.github.naomimyselfandi.xanaduwars.auth.service.RegistrationService;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWT;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.auth.value.UnauthorizedException;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.EntityModelAssembler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AuthControllerTest {

    @Mock
    private EntityModel<Optional<UserDetailsDto>> entityModel;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    @Mock
    private HttpServletResponse response;

    @Mock
    private JWTFactory jwtFactory;

    @Mock
    private AuthService authService;

    @Mock
    private EntityModelAssembler<Optional<UserDetailsDto>> assembler;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private AuthController fixture;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void me(boolean authenticated, SeededRng random) {
        var user = Optional.ofNullable(authenticated ? random.<UserDetailsDto>get() : null);
        when(assembler.toModel(user)).thenReturn(entityModel);
        assertThat(fixture.me(user)).isEqualTo(entityModel);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void login(boolean rememberMe, SeededRng random) {
        var username = random.<Username>get();
        var password = random.<Plaintext>get();
        var dto = random.<UserDetailsDto>get();
        when(authService.loadUserByCredentials(username, password)).thenReturn(Optional.of(dto));
        var accessToken = random.<JWT>get();
        when(jwtFactory.create(dto, JWTPurpose.ACCESS_TOKEN)).thenReturn(accessToken);
        var refreshToken = random.<JWT>get();
        when(jwtFactory.create(dto, JWTPurpose.REFRESH_TOKEN)).thenReturn(refreshToken);
        var request = new LoginRequest(username, password, rememberMe);
        assertThat(fixture.login(request, response))
                .isEqualTo(ResponseEntity.ok(new AccessTokenResponse(accessToken.token())));
        verify(response).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getValue()).satisfies(isRefreshCookie(refreshToken));
        verify(authService).setRememberMe(dto.id(), rememberMe);
    }

    @Test
    void register(SeededRng random) {
        var request = random.<RegistrationRequest>get();
        assertThat(fixture.register(request)).isEqualTo(ResponseEntity.noContent().build());
        verify(registrationService).register(request.username(), request.emailAddress());
    }

    @Test
    void login_WhenTheCredentialsAreInvalid_ThenReturnsA401(SeededRng random) {
        var username = random.<Username>get();
        var password = random.<Plaintext>get();
        when(authService.loadUserByCredentials(username, password)).thenReturn(Optional.empty());
        var request = new LoginRequest(username, password, random.get());
        assertThatThrownBy(() -> fixture.login(request, response))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(UnauthorizedException.INVALID_CREDENTIALS);
        verify(response, never()).addCookie(any());
    }

    @Test
    void refresh(SeededRng random) {
        var originalToken = random.nextString();
        var dto = random.<UserDetailsDto>get();
        when(authService.loadUserByRefreshToken(originalToken)).thenReturn(Optional.of(dto));
        var accessToken = random.<JWT>get();
        when(jwtFactory.create(dto, JWTPurpose.ACCESS_TOKEN)).thenReturn(accessToken);
        var refreshToken = random.<JWT>get();
        when(jwtFactory.create(dto, JWTPurpose.REFRESH_TOKEN)).thenReturn(refreshToken);
        assertThat(fixture.refresh(originalToken, response))
                .isEqualTo(ResponseEntity.ok(new AccessTokenResponse(accessToken.token())));
        verify(response).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getValue()).satisfies(isRefreshCookie(refreshToken));
    }

    @Test
    void refresh_WhenTheRefreshTokenIsInvalid_ThenReturnsA401(SeededRng random) {
        var refreshToken = random.nextUUID().toString();
        when(authService.loadUserByRefreshToken(refreshToken)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.refresh(refreshToken, response))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(UnauthorizedException.INVALID_CREDENTIALS);
        verify(response, never()).addCookie(any());
    }

    @Test
    void logout() {
        assertThat(fixture.logout(response)).isEqualTo(ResponseEntity.noContent().build());
        verify(response).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getValue()).satisfies(isRefreshCookie("", Duration.ZERO));
    }

    private Consumer<Cookie> isRefreshCookie(JWT jwt) {
        return isRefreshCookie(jwt.token(), jwt.duration());
    }

    private static Consumer<Cookie> isRefreshCookie(String token, Duration duration) {
        return cookie -> {
            Assertions.assertThat(cookie.getName()).isEqualTo("refresh_token");
            Assertions.assertThat(cookie.getValue()).isEqualTo(token);
            Assertions.assertThat(cookie.isHttpOnly()).isTrue();
            Assertions.assertThat(cookie.getSecure()).isTrue();
            Assertions.assertThat(cookie.getAttribute("SameSite")).isEqualTo("Strict");
            Assertions.assertThat(cookie.getPath()).isEqualTo("/auth/refresh");
            Assertions.assertThat(cookie.getMaxAge()).isEqualTo(duration.toSeconds());
        };
    }

}
