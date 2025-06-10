package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.auth.Authenticated;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Component
@RequiredArgsConstructor
class AuthenticatedParameterResolver implements HandlerMethodArgumentResolver {

    private final @Lazy AuthService authService;
    private final @Lazy AccountService accountService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authenticated.class)
                && AccountDto.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public @Nullable Object resolveArgument(
            MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        var dto = authService.tryGet().orElse(null);
        var type = parameter.getParameterType();
        if (type.isInstance(dto)) {
            return dto;
        } else if (dto != null) {
            return accountService.find(type.asSubclass(AccountDto.class), dto.getId()).orElseThrow();
        } else if (Objects.requireNonNull(parameter.getParameterAnnotation(Authenticated.class)).required()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } else {
            return null;
        }
    }

}
