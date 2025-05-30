package io.github.naomimyselfandi.xanaduwars.auth.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.PlaintextPassword;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.dto.LoginRequest;
import io.github.naomimyselfandi.xanaduwars.auth.dto.AccessTokenResponse;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.*;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTDurationService.RememberMe;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

/// A controller for logging in and out.
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH = "refresh_token";
    private static final Supplier<ResponseStatusException> UNAUTHORIZED = () ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");

    private final JWTFactory jwtFactory;
    private final JWTValidator jwtValidator;
    private final AccountService accountService;
    private final JWTDurationService jwtDurationService;

    @PostMapping
    public ResponseEntity<AccessTokenResponse> login(
            @RequestBody @Valid LoginRequest request,
            @RequestParam RememberMe rememberMe,
            HttpServletResponse response
    ) {
        var account = getAccountFromCredentials(request.username(), request.password());
        var accessToken = login(rememberMe, response, account);
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            @CookieValue(REFRESH) String token,
            @RequestParam RememberMe rememberMe,
            HttpServletResponse response
    ) {
        var account = getAccountFromRefreshToken(token);
        var accessToken = login(rememberMe, response, account);
        return ResponseEntity.ok(accessToken);
    }

    @DeleteMapping
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        createRefreshCookie(response, "", Duration.ZERO);
        return ResponseEntity.noContent().build();
    }

    private Account getAccountFromCredentials(Username username, PlaintextPassword password) {
        return accountService.find(username, password).orElseThrow(UNAUTHORIZED);
    }

    private Account getAccountFromRefreshToken(String token) {
        return jwtValidator
                .validateToken(token, JWTPurpose.REFRESH_TOKEN, JWTClaim.NONE)
                .map(DecodedJWT::getSubject)
                .map(UUID::fromString)
                .flatMap(accountService::find)
                .orElseThrow(UNAUTHORIZED);
    }

    private AccessTokenResponse login(RememberMe rememberMe, HttpServletResponse response, Account account) {
        var accessToken = getAccessToken(account, rememberMe);
        createRefreshCookie(response, account, rememberMe);
        return new AccessTokenResponse(accessToken);
    }

    private String getAccessToken(Account account, RememberMe rememberMe) {
        var accessDuration = jwtDurationService.duration(account, JWTPurpose.ACCESS_TOKEN, rememberMe);
        return jwtFactory.generateToken(account, JWTPurpose.ACCESS_TOKEN, JWTClaim.NONE, accessDuration);
    }

    private void createRefreshCookie(HttpServletResponse response, Account account, RememberMe rememberMe) {
        var refreshDuration = jwtDurationService.duration(account, JWTPurpose.REFRESH_TOKEN, rememberMe);
        var refreshToken = jwtFactory.generateToken(account, JWTPurpose.REFRESH_TOKEN, JWTClaim.NONE, refreshDuration);
        createRefreshCookie(response, refreshToken, refreshDuration);
    }

    private static void createRefreshCookie(HttpServletResponse response, String token, Duration duration) {
        response.addCookie(createRefreshCookie(token, duration));
    }

    private static Cookie createRefreshCookie(String token, Duration duration) {
        var cookie = new Cookie(REFRESH, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge((int) duration.toSeconds());
        return cookie;
    }

}
