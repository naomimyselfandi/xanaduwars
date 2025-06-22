package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.auth.Authenticated;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
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

import java.lang.reflect.Type;
import java.util.Objects;

@Component
@RequiredArgsConstructor
class AuthenticatedParameterResolver implements HandlerMethodArgumentResolver {

    private static final Type ID_TYPE = new TypeReference<Id<Account>>() {}.getType();

    private final @Lazy AuthService authService;
    private final @Lazy AccountService accountService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authenticated.class) && supportsParameterType(parameter);
    }

    private static boolean supportsParameterType(MethodParameter parameter) {
        return ID_TYPE.equals(parameter.getGenericParameterType())
               || AccountDto.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public @Nullable Object resolveArgument(
            MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) throws NoSuchEntityException {
        var type = parameter.getParameterType();
        var dto = authService.tryGet().orElse(null);
        if (dto == null) {
            if (Objects.requireNonNull(parameter.getParameterAnnotation(Authenticated.class)).required()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            } else {
                return null;
            }
        } else if (type.isInstance(dto)) {
            return dto;
        } else if (type == Id.class) {
            return dto.getId();
        } else {
            return accountService.get(type.asSubclass(AccountDto.class), dto.getId());
        }
    }

}
