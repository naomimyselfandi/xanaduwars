package io.github.naomimyselfandi.xanaduwars.security.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RequestBodyCachingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RequestBodyCachingFilter fixture;

    @Captor
    private ArgumentCaptor<HttpServletRequest> captor;

    @Test
    void doFilterInternal() throws ServletException, IOException {
        fixture.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(captor.capture(), eq(response));
        assertThat(captor.getValue()).isInstanceOf(ContentCachingRequestWrapper.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldNotFilter(boolean alreadyCached) {
        var actualRequest = alreadyCached ? new ContentCachingRequestWrapper(request) : request;
        assertThat(fixture.shouldNotFilter(actualRequest)).isEqualTo(alreadyCached);
    }

}
