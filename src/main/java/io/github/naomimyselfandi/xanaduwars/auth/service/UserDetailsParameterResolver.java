package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.value.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class UserDetailsParameterResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        var type = parameter.getGenericParameterType();
        return isUserDetailsDto(type) || isOptionalUserDetailsDto(type);
    }

    private static boolean isUserDetailsDto(Type type) {
        return type == UserDetailsDto.class;
    }

    private static boolean isOptionalUserDetailsDto(Type type) {
        return type instanceof ParameterizedType it
                && it.getRawType() == Optional.class
                && isUserDetailsDto(it.getActualTypeArguments()[0]);
    }

    @Override
    public @Nullable Object resolveArgument(
            MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        var optional = authService.loadForAuthenticatedUser();
        if (parameter.getParameterType() == Optional.class) {
            return optional;
        } else {
            return optional.orElseThrow(UnauthorizedException::new);
        }
    }

}
