package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.auth.Bypassable;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
class BypassableAspect {

    private final AuthService authService;

    @Around("@annotation(bypassable)")
    public Object maybeBypass(ProceedingJoinPoint joinPoint, Bypassable bypassable) throws Throwable {
        return shouldBypass(bypassable) ? true : joinPoint.proceed();
    }

    private boolean shouldBypass(Bypassable bypassable) {
        return authService
                .tryGet()
                .map(UserDetailsDto::getAuthorities)
                .stream()
                .flatMap(List::stream)
                .anyMatch(Arrays.asList(bypassable.value())::contains);
    }

}
