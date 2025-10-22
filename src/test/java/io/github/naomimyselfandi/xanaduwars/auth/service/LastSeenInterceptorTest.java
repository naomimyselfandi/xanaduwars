package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class LastSeenInterceptorTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthService authService;

    @Mock
    private Clock clock;

    @InjectMocks
    private LastSeenInterceptor fixture;

    @Test
    void preHandle(SeededRng random) {
        var user = random.<UserDetailsDto>get();
        var instant = random.nextInstant();
        var handler = new Object();
        when(authService.loadForAuthenticatedUser()).thenReturn(Optional.of(user));
        when(clock.instant()).thenReturn(instant);
        assertThat(fixture.preHandle(httpServletRequest, httpServletResponse, handler)).isTrue();
        verifyNoInteractions(accountRepository);
        fixture.flush();
        verify(accountRepository).updateLastSeenAtById(user.id(), instant);
        fixture.flush();
        verifyNoMoreInteractions(accountRepository);
    }

}
