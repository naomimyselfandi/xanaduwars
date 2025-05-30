package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.auth.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

@Component
@RequestMapping
@RequiredArgsConstructor
class CurrentAccountParameterResolver implements HandlerMethodArgumentResolver {

    private final CurrentAccountService currentAccountService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentAccount.class) && switch (parameter.getGenericParameterType()) {
            case Class<?> c -> c == Account.class;
            case ParameterizedType p -> p.getRawType() == Optional.class && p.getActualTypeArguments()[0] == Account.class;
            default -> false;
        };
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        if (parameter.getParameterType() == Optional.class) {
            return currentAccountService.tryGet();
        } else {
            return currentAccountService.get();
        }
    }

}
