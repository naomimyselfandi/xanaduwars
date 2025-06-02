package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountReference;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AccountReferenceResolverImplTest {

    private interface Helper extends AccountDto {}

    @Mock
    private Helper dto;

    @Mock
    private AuthService authService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountReferenceResolverImpl fixture;

    @Test
    void resolve_ById(SeededRng random) {
        var id = random.nextAccountId();
        when(accountService.find(Helper.class, id)).thenReturn(Optional.of(dto));
        assertThat(fixture.resolve(Helper.class, id)).contains(dto);
    }

    @Test
    void resolve_CurrentAccount(SeededRng random) {
        var id = random.nextAccountId();
        var details = new UserDetailsDto();
        details.setId(id);
        when(authService.tryGet()).thenReturn(Optional.of(details));
        when(accountService.find(Helper.class, id)).thenReturn(Optional.of(dto));
        assertThat(fixture.resolve(Helper.class, AccountReference.ME)).contains(dto);
    }

    @Test
    void resolve_CurrentAccount_AsUserDetailsDto(SeededRng random) {
        var id = random.nextAccountId();
        var details = new UserDetailsDto();
        details.setId(id);
        when(authService.tryGet()).thenReturn(Optional.of(details));
        assertThat(fixture.resolve(UserDetailsDto.class, AccountReference.ME)).contains(details);
    }

}
