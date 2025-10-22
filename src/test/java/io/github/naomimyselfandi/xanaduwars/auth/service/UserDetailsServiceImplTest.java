package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
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
    private AuthService authService;

    @InjectMocks
    private UserDetailsServiceImpl fixture;

    @Test
    void loadUserByUsername(SeededRng random) {
        var username = random.<Username>get();
        var dto = random.<UserDetailsDto>get();
        when(authService.loadUserByName(username)).thenReturn(Optional.of(dto));
        assertThat(fixture.loadUserByUsername(username.toString())).isEqualTo(dto);
    }

    @Test
    void loadUserByUsername_WhenTheUserDoesNotExist_ThenThrow(SeededRng random) {
        var username = random.<Username>get();
        when(authService.loadUserByName(username)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.loadUserByUsername(username.toString()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: %s", username);
    }

}
