package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.audit.service.AuditService;
import io.github.naomimyselfandi.xanaduwars.auth.Bypassable;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import lombok.experimental.StandardException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class BypassableAspectTest {

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private Bypassable bypassable;

    private Role role0, role1;

    @Mock
    private AuthService authService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private BypassableAspect fixture;

    @BeforeEach
    void setup(SeededRng random) {
        role0 = random.get();
        role1 = random.not(role0);
        when(bypassable.value()).thenReturn(new Role[]{role0, role1});
    }

    @Test
    void apply_WhenTheMethodReturnsTrue_ThenDoesNothing() throws Throwable {
        when(proceedingJoinPoint.proceed()).thenReturn(true);
        assertThat(fixture.apply(proceedingJoinPoint, bypassable)).isEqualTo(true);
        verifyNoInteractions(auditService);
    }

    @Test
    void apply_WhenTheMethodReturnsNull_ThenDoesNothing() throws Throwable {
        when(proceedingJoinPoint.proceed()).thenReturn(null);
        assertThat(fixture.apply(proceedingJoinPoint, bypassable)).isNull();
        verifyNoInteractions(auditService);
    }

    @Test
    void apply_WhenTHeMethodThrowsAnOrdinaryException_ThenDoesNothing() throws Throwable {
        var exception = new Exception();
        when(proceedingJoinPoint.proceed()).thenThrow(exception);
        assertThatThrownBy(() -> fixture.apply(proceedingJoinPoint, bypassable)).isEqualTo(exception);
        verifyNoInteractions(auditService);
    }

    @Test
    void apply_WhenTHeMethodThrowsANon403Exception_ThenDoesNothing() throws Throwable {
        @StandardException
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        class SomeException extends Exception {}
        var exception = new SomeException();
        when(proceedingJoinPoint.proceed()).thenThrow(exception);
        assertThatThrownBy(() -> fixture.apply(proceedingJoinPoint, bypassable)).isEqualTo(exception);
        verifyNoInteractions(auditService);
    }

    @Test
    void apply_WhenTheMethodReturnsFalseAndThereIsNoUser_ThenDoesNothing() throws Throwable {
        when(proceedingJoinPoint.proceed()).thenReturn(false);
        when(authService.tryGet()).thenReturn(Optional.empty());
        assertThat(fixture.apply(proceedingJoinPoint, bypassable)).isEqualTo(false);
        verifyNoInteractions(auditService);
    }

    @Test
    void apply_WhenTheMethodReturnsFalseAndTheUserHasNoRelevantRole_ThenDoesNothing(SeededRng random) throws Throwable {
        var details = new UserDetailsDto();
        details.setAuthorities(List.of(random.not(role0, role1)));
        when(proceedingJoinPoint.proceed()).thenReturn(false);
        when(authService.tryGet()).thenReturn(Optional.empty());
        assertThat(fixture.apply(proceedingJoinPoint, bypassable)).isEqualTo(false);
        verifyNoInteractions(auditService);
    }

    @Test
    void apply_WhenTheMethodReturnsFalseAndTheUserHasARelevantRole_ThenBypasses(SeededRng random) throws Throwable {
        var details = new UserDetailsDto();
        details.setAuthorities(List.of(random.pick(role0, role1), random.not(role0, role1)));
        when(proceedingJoinPoint.proceed()).thenReturn(false);
        when(authService.tryGet()).thenReturn(Optional.of(details));
        assertThat(fixture.apply(proceedingJoinPoint, bypassable)).isEqualTo(true);
        verify(auditService).log("BYPASS_ACCESS_CHECK", null);
    }

    @Test
    void apply_WhenTHeMethodThrowsA403ExceptionAndThereIsNoUser_ThenDoesNothing() throws Throwable {
        @StandardException
        @ResponseStatus(HttpStatus.FORBIDDEN)
        class SomeException extends Exception {}
        var exception = new SomeException();
        when(authService.tryGet()).thenReturn(Optional.empty());
        when(proceedingJoinPoint.proceed()).thenThrow(exception);
        assertThatThrownBy(() -> fixture.apply(proceedingJoinPoint, bypassable)).isEqualTo(exception);
        verifyNoInteractions(auditService);
    }

    @Test
    void apply_WhenTheMethodThrowsA403ExceptionAndTheUserHasNoRelevantRole_ThenDoesNothing(SeededRng random)
            throws Throwable {
        @StandardException
        @ResponseStatus(HttpStatus.FORBIDDEN)
        class SomeException extends Exception {}
        var exception = new SomeException();
        var details = new UserDetailsDto();
        details.setAuthorities(List.of(random.not(role0, role1)));
        when(authService.tryGet()).thenReturn(Optional.of(details));
        when(proceedingJoinPoint.proceed()).thenThrow(exception);
        assertThatThrownBy(() -> fixture.apply(proceedingJoinPoint, bypassable)).isEqualTo(exception);
        verifyNoInteractions(auditService);
    }

    @Test
    void apply_WhenTheMethodThrowsA403ExceptionAndTheUserHasARelevantRole_ThenBypasses(SeededRng random)
            throws Throwable {
        @StandardException
        @ResponseStatus(HttpStatus.FORBIDDEN)
        class SomeException extends Exception {}
        var exception = new SomeException();
        var details = new UserDetailsDto();
        details.setAuthorities(List.of(random.pick(role0, role1), random.not(role0, role1)));
        when(authService.tryGet()).thenReturn(Optional.of(details));
        when(proceedingJoinPoint.proceed()).thenThrow(exception);
        assertThatCode(() -> fixture.apply(proceedingJoinPoint, bypassable)).doesNotThrowAnyException();
        verify(auditService).log("BYPASS_ACCESS_CHECK", null);
    }

    @Test
    void apply_CapturesTheMethod(SeededRng random) throws Throwable {
        var method = BypassableAspectTest.class.getDeclaredMethod("apply_CapturesTheMethod", SeededRng.class);
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        var details = new UserDetailsDto();
        details.setAuthorities(List.of(random.pick(role0, role1), random.not(role0, role1)));
        when(proceedingJoinPoint.proceed()).thenReturn(false);
        when(authService.tryGet()).thenReturn(Optional.of(details));
        assertThat(fixture.apply(proceedingJoinPoint, bypassable)).isEqualTo(true);
        verify(auditService).log("BYPASS_ACCESS_CHECK", method);
    }

}
