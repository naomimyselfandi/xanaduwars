package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.value.UnauthorizedException;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UserDetailsParameterResolverTest {

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private ModelAndViewContainer mavContainer;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private WebDataBinderFactory binderFactory;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserDetailsParameterResolver fixture;

    @MethodSource
    @ParameterizedTest
    void supportsParameter(Type type, boolean expected) {
        when(methodParameter.getGenericParameterType()).thenReturn(type);
        assertThat(fixture.supportsParameter(methodParameter)).isEqualTo(expected);
    }

    @Test
    void resolveArgument_WhenTheUserDetailsAreRequiredAndAvailable_ThenReturnsThem(SeededRng random) {
        var dto = random.<UserDetailsDto>get();
        when(authService.loadForAuthenticatedUser()).thenReturn(Optional.of(dto));
        when((Object) methodParameter.getParameterType()).thenReturn(UserDetailsDto.class);
        assertThat(fixture.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory))
                .isEqualTo(dto);
    }

    @Test
    void resolveArgument_WhenTheUserDetailsAreRequiredAndAbsent_ThenThrows() {
        when(authService.loadForAuthenticatedUser()).thenReturn(Optional.empty());
        when((Object) methodParameter.getParameterType()).thenReturn(UserDetailsDto.class);
        assertThatThrownBy(() -> fixture.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(UnauthorizedException.INVALID_CREDENTIALS);
    }

    @Test
    void resolveArgument_WhenTheUserDetailsAreOptionalAndAvailable_ThenReturnsThem(SeededRng random) {
        var dto = random.<UserDetailsDto>get();
        when(authService.loadForAuthenticatedUser()).thenReturn(Optional.of(dto));
        when((Object) methodParameter.getParameterType()).thenReturn(Optional.class);
        assertThat(fixture.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory))
                .isEqualTo(Optional.of(dto));
    }

    @Test
    void resolveArgument_WhenTheUserDetailsAreOptionalAndAbsent_ThenReturnsNothing() {
        when(authService.loadForAuthenticatedUser()).thenReturn(Optional.empty());
        when((Object) methodParameter.getParameterType()).thenReturn(Optional.class);
        assertThat(fixture.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory))
                .isEqualTo(Optional.empty());
    }

    private static Stream<Arguments> supportsParameter() {
        return Stream.of(
                arguments(UserDetailsDto.class, true),
                arguments(new TypeReference<Optional<UserDetailsDto>>() {}.getType(), true),
                arguments(UserDetails.class, false),
                arguments(new TypeReference<Optional<UserDetails>>() {}.getType(), false),
                arguments(new TypeReference<List<UserDetailsDto>>() {}.getType(), false),
                arguments(Object.class, false),
                arguments(new TypeReference<Optional<Object>>() {}.getType(), false)
        );
    }

}
