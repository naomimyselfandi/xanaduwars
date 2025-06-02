package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.auth.Bypassable;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class BypassableAspectTest {

    @Mock
    private Bypassable annotation;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private AuthService authService;

    @InjectMocks
    private BypassableAspect fixture;

    @Test
    void maybeBypass_WhenTheUserHasOneOfTheGivenRoles_ThenTrue(SeededRng random) throws Throwable {
        var roles = random.shuffle(Role.values());
        var dto = new UserDetailsDto();
        dto.setAuthorities(roles.subList(0, 2));
        when(authService.tryGet()).thenReturn(Optional.of(dto));
        when(annotation.value()).thenReturn(new Role[]{roles.get(1), roles.get(3)});
        assertThat(fixture.maybeBypass(joinPoint, annotation)).isEqualTo(true);
        verify(joinPoint, never()).proceed();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void maybeBypass_WhenTheUserHasNoneOfTheGivenRoles_ThenDelegates(boolean expected, SeededRng random)
        throws Throwable {
        var roles = random.shuffle(Role.values());
        var dto = new UserDetailsDto();
        dto.setAuthorities(roles.subList(0, 2));
        when(authService.tryGet()).thenReturn(Optional.of(dto));
        when(annotation.value()).thenReturn(new Role[]{roles.get(2), roles.get(4)});
        when(joinPoint.proceed()).thenReturn(expected);
        assertThat(fixture.maybeBypass(joinPoint, annotation)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void maybeBypass_WhenNoUserIsAvailable_ThenDelegates(boolean expected, SeededRng random) throws Throwable {
        var roles = random.shuffle(Role.values());
        when(authService.tryGet()).thenReturn(Optional.empty());
        when(annotation.value()).thenReturn(new Role[]{roles.get(2), roles.get(4)});
        when(joinPoint.proceed()).thenReturn(expected);
        assertThat(fixture.maybeBypass(joinPoint, annotation)).isEqualTo(expected);
    }

}
