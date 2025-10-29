package io.github.naomimyselfandi.xanaduwars.security.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
class RequestBodyCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        filterChain.doFilter(new ContentCachingRequestWrapper(request), response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request instanceof ContentCachingRequestWrapper;
    }

}
