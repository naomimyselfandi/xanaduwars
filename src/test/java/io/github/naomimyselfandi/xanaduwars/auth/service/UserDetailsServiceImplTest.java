package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UserDetailsServiceImplTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private UserDetailsServiceImpl fixture;

    @Test
    void loadUserByUsername(SeededRng random) {
        var account = new HumanAccount();
        var username = random.nextUsername();
        when(accountService.find(username)).thenReturn(Optional.of(account));
        assertThat(fixture.loadUserByUsername(username.toString())).isEqualTo(new UserDetailsImpl(account));
    }

    @Test
    void loadUserByUsername_WhenTheUserDoesNotExist_ThenThrow(SeededRng random) {
        var username = random.nextUsername();
        assertThatThrownBy(() -> fixture.loadUserByUsername(username.toString()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: %s", username);
    }

}
