package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.BotAccount;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.PlaintextAPIKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component("apiKeyAuthenticationFilter")
class APIKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String BASIC = "Basic ";
    private static final int BASIC_LENGTH = BASIC.length();

    private final HashService hashService;
    private final AccountService accountService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                    .filter(header -> header.startsWith(BASIC))
                    .map(header -> header.substring(BASIC_LENGTH))
                    .map(token -> new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8))
                    .map(decoded -> decoded.split(":", 2))
                    .filter(parts -> parts.length == 2)
                    .flatMap(parts -> accountService
                            .find(UUID.fromString(parts[0]))
                            .filter(account -> {
                                var plaintext = new PlaintextAPIKey(parts[1]);
                                var hash = account.authenticationSecret();
                                return hashService.test(plaintext, hash);
                            }))
                    .filter(APIKeyAuthenticationFilter::rejectNonBotAccount)
                    .ifPresent(account -> JWTAuthenticationFilter.authenticate(account, request));
        } catch (IllegalArgumentException _) {}
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    private static boolean rejectNonBotAccount(Account account) {
        if (account instanceof BotAccount) {
            return true;
        } else {
            log.warn("Ignoring API key for non-bot account {}.", account);
            return false;
        }
    }

}
