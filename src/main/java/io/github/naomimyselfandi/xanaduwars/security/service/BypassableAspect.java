package io.github.naomimyselfandi.xanaduwars.security.service;

import io.github.naomimyselfandi.xanaduwars.account.service.RoleService;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.security.Bypassable;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.Optional;

import static io.github.naomimyselfandi.xanaduwars.security.Audited.MissingAuthPolicy;

@Aspect
@Component
@RequiredArgsConstructor
class BypassableAspect {

    private final AuthService authService;
    private final AuditService auditService;
    private final RoleService roleService;

    @Around("@annotation(bypassable)")
    public @Nullable Object apply(ProceedingJoinPoint joinPoint, Bypassable bypassable) throws Throwable {
        try {
            var result = joinPoint.proceed();
            if (Boolean.FALSE.equals(result) && canBypass(bypassable)) {
                recordBypass(joinPoint);
                return true;
            } else {
                return result;
            }
        } catch (Exception e) {
            if (isSecurityException(e) && canBypass(bypassable)) {
                recordBypass(joinPoint);
                return null;
            } else {
                throw e;
            }
        }
    }

    private boolean canBypass(Bypassable bypassable) {
        return authService
                .loadForAuthenticatedUser()
                .filter(user -> Arrays
                        .stream(bypassable.value())
                        .anyMatch(role -> roleService.hasRole(user, role)))
                .isPresent();
    }

    private static boolean isSecurityException(Exception exception) {
        return Optional
                .ofNullable(exception.getClass().getAnnotation(ResponseStatus.class))
                .filter(status -> status.value() == HttpStatus.FORBIDDEN)
                .isPresent();
    }

    private void recordBypass(ProceedingJoinPoint joinPoint) {
        var method = joinPoint.getSignature() instanceof MethodSignature sig ? sig.getMethod() : null;
        auditService.log("BYPASS_ACCESS_CHECK", method, MissingAuthPolicy.WRITE_INCOMPLETE_ENTRY_AND_WARN);
    }

}
