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

@Component
@RequiredArgsConstructor
class AuthenticatedResolver implements HandlerMethodArgumentResolver {

    private final @Lazy AuthService authService;
    private final @Lazy AccountService accountService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authenticated.class)
                && AccountDto.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        var dto = authService.tryGet().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        var type = parameter.getParameterType();
        if (type.isInstance(dto)) {
            return dto;
        } else {
            return accountService.find(type.asSubclass(AccountDto.class), dto.getId()).orElseThrow();
        }
    }

}
