package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTClaim;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTValidator;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTPurpose;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component("jwtAuthenticationFilter")
class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";
    private static final int BEARER_LENGTH = BEARER.length();

    private final JWTValidator jwtValidator;
    private final AccountService accountService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(header -> header.startsWith(BEARER))
                .map(header -> header.substring(BEARER_LENGTH))
                .flatMap(token -> jwtValidator.validateToken(token, JWTPurpose.ACCESS_TOKEN, JWTClaim.NONE))
                .map(DecodedJWT::getSubject)
                .map(UUID::fromString)
                .map(id -> accountService.find(UserDetailsDto.class, id).orElseGet(() -> {
                    log.warn("Got nonexistent account ID {}.", id);
                    return null;
                }))
                .ifPresent(account -> authenticate(account, request));
        filterChain.doFilter(request, response);
    }

    static void authenticate(UserDetails principal, HttpServletRequest request) {
        var authorities = principal.getAuthorities();
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/auth/");
    }

}
