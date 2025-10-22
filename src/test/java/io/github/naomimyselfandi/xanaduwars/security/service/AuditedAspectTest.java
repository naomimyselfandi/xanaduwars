package io.github.naomimyselfandi.xanaduwars.security.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.security.Audited;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AuditedAspectTest {

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private Audited audited;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditedAspect fixture;

    @EnumSource
    @ParameterizedTest
    void apply(Audited.MissingAuthPolicy missingAuthPolicy, SeededRng random) throws Throwable {
        var method = AuditedAspectTest.class
                .getDeclaredMethod("apply", Audited.MissingAuthPolicy.class, SeededRng.class);
        var object = new Object();
        when(proceedingJoinPoint.proceed()).thenReturn(object);
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        var name = random.nextString();
        when(audited.value()).thenReturn(name);
        when(audited.whenNotAuthenticated()).thenReturn(missingAuthPolicy);
        assertThat(fixture.apply(proceedingJoinPoint, audited)).isEqualTo(object);
        verify(auditService).log(name, method, missingAuthPolicy);
    }

    @EnumSource
    @ParameterizedTest
    void apply_ToleratesMissingMethod(Audited.MissingAuthPolicy missingAuthPolicy, SeededRng random)
            throws Throwable {
        var object = new Object();
        when(proceedingJoinPoint.proceed()).thenReturn(object);
        var name = random.nextString();
        when(audited.value()).thenReturn(name);
        when(audited.whenNotAuthenticated()).thenReturn(missingAuthPolicy);
        assertThat(fixture.apply(proceedingJoinPoint, audited)).isEqualTo(object);
        verify(auditService).log(name, null, missingAuthPolicy);
    }

}
