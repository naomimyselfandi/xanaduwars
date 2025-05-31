package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AuthServiceImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl fixture;

    @BeforeEach
    void setup() {
        clearSecurityContext();
    }

    @AfterAll
    static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tryGet(SeededRng random) {
        var principal = new UserDetailsDto();
        principal.setId(random.nextUUID());
        var authentication = new UsernamePasswordAuthenticationToken(principal, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThat(fixture.tryGet()).contains(principal);
    }

    @Test
    void tryGet_WhenTheSecurityContextIsEmpty_ThenReturnsAnEmptyResult() {
        assertThat(fixture.tryGet()).isEmpty();
    }

    @Test
    void tryGet_WhenTheSecurityContextHoldsAnUnexpectedPrincipal_ThenReturnsAnEmptyResult() {
        var principal = "please let me in, I am not hacker";
        var authentication = new UsernamePasswordAuthenticationToken(principal, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThat(fixture.tryGet()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void find(boolean matches, SeededRng random) {
        var username = random.nextUsername();
        var password = random.nextPassword();
        var plaintext = random.nextPlaintextPassword();
        var dto = new UserDetailsDto();
        dto.setPassword(password.text());
        when(accountService.find(UserDetailsDto.class, username)).thenReturn(Optional.of(dto));
        when(passwordEncoder.matches(plaintext.text(), password.text())).thenReturn(matches);
        if (matches) {
            assertThat(fixture.find(username, plaintext)).contains(dto);
        } else {
            assertThat(fixture.find(username, plaintext)).isEmpty();
        }
    }

}
