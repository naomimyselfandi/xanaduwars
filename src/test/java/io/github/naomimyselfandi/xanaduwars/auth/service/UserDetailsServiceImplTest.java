package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
    void loadUserByUsername(SeededRng random) throws NoSuchEntityException {
        var username = random.<Username>get();
        var dto = new UserDetailsDto();
        dto.setId(random.get());
        when(accountService.get(UserDetailsDto.class, username)).thenReturn(dto);
        assertThat(fixture.loadUserByUsername(username.toString())).isEqualTo(dto);
    }

    @Test
    void loadUserByUsername_WhenTheUserDoesNotExist_ThenThrow(SeededRng random) throws NoSuchEntityException {
        var username = random.<Username>get();
        when(accountService.get(UserDetailsDto.class, username)).thenThrow(NoSuchEntityException.class);
        assertThatThrownBy(() -> fixture.loadUserByUsername(username.toString()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: %s", username);
    }

}
