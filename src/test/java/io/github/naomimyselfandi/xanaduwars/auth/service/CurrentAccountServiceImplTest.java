package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CurrentAccountServiceImplTest {

    private CurrentAccountServiceImpl fixture;

    @BeforeEach
    void setup() {
        clearSecurityContext();
        fixture = new CurrentAccountServiceImpl();
    }

    @AfterAll
    static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tryGet() {
        var account = new HumanAccount();
        var principal = new UserDetailsImpl(account);
        var authentication = new UsernamePasswordAuthenticationToken(principal, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThat(fixture.tryGet()).contains(account);
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

    @AfterEach
    void get() {
        var account = fixture.tryGet();
        if (account.isPresent()) {
            assertThat(fixture.get()).isEqualTo(account.get());
        } else {
            assertThatThrownBy(fixture::get).isInstanceOf(AccessDeniedException.class);
        }
    }

}
