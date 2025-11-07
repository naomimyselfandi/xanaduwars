package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.auth.value.UnauthorizedException;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AuthServiceImplTest {

    private UserDetailsDto dto;

    @Mock
    private DecodedJWT decodedJWT;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Supplier<SecurityContext> securityContextSupplier;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTValidator jwtValidator;

    @InjectMocks
    private AuthServiceImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        lenient().when(securityContextSupplier.get()).thenReturn(securityContext);
        dto = random.get();
    }

    @Test
    void loadForAuthenticatedUser() {
        var authentication = new UsernamePasswordAuthenticationToken(dto, null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        assertThat(fixture.loadForAuthenticatedUser()).contains(dto);
    }

    @Test
    void loadForAuthenticatedUser_WhenNoCredentialsAreAvailable_ThenReturnsNothing() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThat(fixture.loadForAuthenticatedUser()).isEmpty();
    }

    @Test
    void loadForAuthenticatedUser_WhenTheCredentialsAreAnotherType_ThenReturnsNothing() {
        var authentication = new UsernamePasswordAuthenticationToken(new Object(), null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        assertThat(fixture.loadForAuthenticatedUser()).isEmpty();
    }

    @Test
    void loadUserById(SeededRng random) {
        var id = random.<Id<Account>>get();
        when(accountRepository.findById(id, UserDetailsDto.class)).thenReturn(Optional.of(dto));
        assertThat(fixture.loadUserById(id)).contains(dto);
    }

    @Test
    void loadUserByName(SeededRng random) {
        var username = random.<Username>get();
        when(accountRepository.findByCanonicalUsername(username.toCanonicalForm(), UserDetailsDto.class))
                .thenReturn(Optional.of(dto));
        assertThat(fixture.loadUserByName(username)).contains(dto);
    }

    @Test
    void loadUserByEmailAddress(SeededRng random) {
        var emailAddress = random.<EmailAddress>get();
        when(accountRepository.findByEmailAddress(emailAddress, UserDetailsDto.class)) .thenReturn(Optional.of(dto));
        assertThat(fixture.loadUserByEmailAddress(emailAddress)).contains(dto);
    }

    @Test
    void loadUserByCredentials(SeededRng random) {
        var username = random.<Username>get();
        when(accountRepository.findByCanonicalUsername(username.toCanonicalForm(), UserDetailsDto.class))
                .thenReturn(Optional.of(dto));
        var password = random.<Plaintext>get();
        when(passwordEncoder.matches(password.text(), dto.getPassword())).thenReturn(true);
        assertThat(fixture.loadUserByCredentials(username, password)).contains(dto);
    }

    @Test
    void loadUserByCredentials_WhenThePasswordDoesNotMatch_ThenEmpty(SeededRng random) {
        var username = random.<Username>get();
        when(accountRepository.findByCanonicalUsername(username.toCanonicalForm(), UserDetailsDto.class))
                .thenReturn(Optional.of(dto));
        var password = random.<Plaintext>get();
        when(passwordEncoder.matches(password.text(), dto.getPassword())).thenReturn(false);
        assertThat(fixture.loadUserByCredentials(username, password)).isEmpty();
    }

    @Test
    void loadUserByRefreshToken(SeededRng random) {
        var refreshToken = random.nextString();
        when(jwtValidator.validate(refreshToken, JWTPurpose.REFRESH_TOKEN)).thenReturn(Optional.of(decodedJWT));
        var id = random.<Id<Account>>get();
        when(decodedJWT.getSubject()).thenReturn(id.toString());
        when(accountRepository.findById(id, UserDetailsDto.class)).thenReturn(Optional.of(dto));
        assertThat(fixture.loadUserByRefreshToken(refreshToken)).contains(dto);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void setRememberMe(boolean rememberMe, SeededRng random) {
        var id = random.<Id<Account>>get();
        fixture.setRememberMe(id, rememberMe);
        verify(accountRepository).updateRememberMeById(id, rememberMe);
    }

    @Test
    void get() {
        var authentication = new UsernamePasswordAuthenticationToken(dto, null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        assertThat(fixture.get()).isEqualTo(dto);
    }

    @Test
    void get_WhenNoCredentialsAreAvailable_ThenThrows() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThatThrownBy(fixture::get).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void get_WhenTheCredentialsAreAnotherType_ThenThrows() {
        var authentication = new UsernamePasswordAuthenticationToken(new Object(), null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        assertThatThrownBy(fixture::get).isInstanceOf(UnauthorizedException.class);
    }

}
