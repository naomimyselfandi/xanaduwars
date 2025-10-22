package io.github.naomimyselfandi.xanaduwars.security.service;

import io.github.naomimyselfandi.xanaduwars.security.Audited;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
class AuditedAspect {

    private final AuditService auditService;

    @Around("@annotation(audited)")
    public @Nullable Object apply(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        var method = joinPoint.getSignature() instanceof MethodSignature sig ? sig.getMethod() : null;
        auditService.log(audited.value(), method, audited.whenNotAuthenticated());
        return joinPoint.proceed();
    }

}
