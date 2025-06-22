package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.auth.Authenticated;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AuthenticatedParameterResolverTest {

    private interface Helper extends AccountDto {}

    @Mock
    private Authenticated annotation;

    @Mock
    private MethodParameter parameter;

    @Mock
    private AuthService authService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AuthenticatedParameterResolver fixture;

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto,true
            false,io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto,false
            true,io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto,true
            false,io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto,false
            true,io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto,true
            false,io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto,false
            true,io.github.naomimyselfandi.xanaduwars.account.dto.FullAccountDto,true
            false,io.github.naomimyselfandi.xanaduwars.account.dto.FullAccountDto,false
            true,java.lang.Object,false
            false,java.lang.Object,false
            true,java.lang.String,false
            false,java.lang.String,false
            true,java.util.UUID,false
            false,java.util.UUID,false
            """)
    void supportsParameter(boolean annotated, Class<?> type, boolean expected) {
        when(parameter.hasParameterAnnotation(Authenticated.class)).thenReturn(annotated);
        doReturn(type).when(parameter).getParameterType();
        assertThat(fixture.supportsParameter(parameter)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void supportsParameter_AccountId(boolean annotated) {
        when(parameter.hasParameterAnnotation(Authenticated.class)).thenReturn(annotated);
        when(parameter.getGenericParameterType()).thenReturn(new TypeReference<Id<Account>>() {}.getType());
        assertThat(fixture.supportsParameter(parameter)).isEqualTo(annotated);
    }

    @ParameterizedTest
    @ValueSource(classes = {UserDetailsDto.class, AccountDto.class})
    void resolveArgument_AsUserDetailsDto(Class<?> type, SeededRng random) throws NoSuchEntityException {
        var dto = random.<UserDetailsDto>get();
        when(authService.tryGet()).thenReturn(Optional.of(dto));
        doReturn(type).when(parameter).getParameterType();
        assertThat(fixture.resolveArgument(parameter, mock(), mock(), mock())).isEqualTo(dto);
    }

    @Test
    void resolveArgument_AsAnotherDto(SeededRng random) throws NoSuchEntityException {
        var helper = mock(Helper.class);
        var dto = random.<UserDetailsDto>get();
        when(authService.tryGet()).thenReturn(Optional.of(dto));
        doReturn(Helper.class).when(parameter).getParameterType();
        when(accountService.get(Helper.class, dto.getId())).thenReturn(helper);
        assertThat(fixture.resolveArgument(parameter, mock(), mock(), mock())).isEqualTo(helper);
    }

    @Test
    void resolveArgument_AsId(SeededRng random) throws NoSuchEntityException {
        var dto = random.<UserDetailsDto>get();
        when(authService.tryGet()).thenReturn(Optional.of(dto));
        doReturn(Id.class).when(parameter).getParameterType();
        assertThat(fixture.resolveArgument(parameter, mock(), mock(), mock())).isEqualTo(dto.getId());
    }

    @Test
    void resolveArgument_WhenAnAccountIsRequiredAndUnavailable_ThenThrows() {
        when(parameter.getParameterAnnotation(Authenticated.class)).thenReturn(annotation);
        when(annotation.required()).thenReturn(true);
        when(authService.tryGet()).thenReturn(Optional.empty());
        doReturn(Helper.class).when(parameter).getParameterType();
        assertThatThrownBy(() -> fixture.resolveArgument(parameter, mock(), mock(), mock()))
                .isInstanceOfSatisfying(
                        ResponseStatusException.class,
                        it -> assertThat(it.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void resolveArgument_WhenAnAccountIsOptionalAndUnavailable_ThenReturnsNull() throws NoSuchEntityException {
        when(parameter.getParameterAnnotation(Authenticated.class)).thenReturn(annotation);
        when(annotation.required()).thenReturn(false);
        when(authService.tryGet()).thenReturn(Optional.empty());
        doReturn(Helper.class).when(parameter).getParameterType();
        assertThat(fixture.resolveArgument(parameter, mock(), mock(), mock())).isNull();
    }

}
