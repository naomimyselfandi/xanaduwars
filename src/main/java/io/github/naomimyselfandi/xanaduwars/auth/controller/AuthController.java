package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.auth.service.JWTFactory;
import io.github.naomimyselfandi.xanaduwars.auth.service.RegistrationService;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.auth.value.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class AuthController {

    private static final String REFRESH_TOKEN = "refresh_token";

    private final JWTFactory jwtFactory;
    private final AuthService authService;
    private final RegistrationService registrationService;

    @GetMapping("/me")
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public ResponseEntity<UserDetailsDto> me(Optional<UserDetailsDto> me) {
        return me.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegistrationRequest request) {
        registrationService.register(request.username(), request.emailAddress());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response
    ) {
        var username = request.username();
        var password = request.password();
        var account = authService.loadUserByCredentials(username, password).orElseThrow(UnauthorizedException::new);
        var accessToken = login(response, account);
        authService.setRememberMe(account.id(), request.rememberMe());
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            @CookieValue(REFRESH_TOKEN) String token,
            HttpServletResponse response
    ) {
        var account = authService.loadUserByRefreshToken(token).orElseThrow(UnauthorizedException::new);
        var accessToken = login(response, account);
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        createRefreshCookie(response, "", Duration.ZERO);
        return ResponseEntity.noContent().build();
    }

    private AccessTokenResponse login(HttpServletResponse response, UserDetailsDto dto) {
        var accessToken = jwtFactory.create(dto, JWTPurpose.ACCESS_TOKEN).token();
        createRefreshCookie(response, dto);
        return new AccessTokenResponse(accessToken);
    }

    private void createRefreshCookie(HttpServletResponse response, UserDetailsDto dto) {
        var jwt = jwtFactory.create(dto, JWTPurpose.REFRESH_TOKEN);
        createRefreshCookie(response, jwt.token(), jwt.duration());
    }

    private static void createRefreshCookie(HttpServletResponse response, String token, Duration duration) {
        var cookie = createRefreshCookie(token, duration);
        response.addCookie(cookie);
    }

    private static Cookie createRefreshCookie(String token, Duration duration) {
        var cookie = new Cookie(REFRESH_TOKEN, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge((int) duration.toSeconds());
        return cookie;
    }

}
