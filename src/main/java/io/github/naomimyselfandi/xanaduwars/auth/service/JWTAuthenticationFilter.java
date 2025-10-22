package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Component("jwtAuthenticationFilter")
class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";
    private static final int BEARER_LENGTH = BEARER.length();

    private final AuthService authService;
    private final JWTValidator jwtValidator;
    private final Supplier<SecurityContext> securityContextSupplier;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(header -> header.startsWith(BEARER))
                .map(header -> header.substring(BEARER_LENGTH))
                .flatMap(token -> jwtValidator.validate(token, JWTPurpose.ACCESS_TOKEN))
                .map(DecodedJWT::getSubject)
                .map(UUID::fromString)
                .map(Id<Account>::new)
                .flatMap(authService::loadUserById)
                .ifPresent(account -> authenticate(account, request));
        filterChain.doFilter(request, response);
    }

    private void authenticate(UserDetails principal, HttpServletRequest request) {
        var authorities = principal.getAuthorities();
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContextSupplier.get().setAuthentication(authentication);
    }

}
