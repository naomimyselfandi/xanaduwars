package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.*;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController fixture;

    @Test
    void getBase(SeededRng random) throws NoSuchEntityException {
        var id = random.<Id<Account>>get();
        var out = random.<BaseAccountDto>get();
        when(accountService.get(BaseAccountDto.class, id)).thenReturn(out);
        assertThat(fixture.getBase(id)).isEqualTo(ResponseEntity.ok(out));
    }

    @Test
    void getMe(SeededRng random) {
        var out = random.<FullAccountDto>get();
        assertThat(fixture.getMe(out)).isEqualTo(ResponseEntity.ok(out));
    }

    @Test
    void getFull(SeededRng random) throws NoSuchEntityException {
        var id = random.<Id<Account>>get();
        var dto = random.<FullAccountDto>get();
        when(accountService.get(FullAccountDto.class, id)).thenReturn(dto);
        assertThat(fixture.getFull(id)).isEqualTo(ResponseEntity.ok(dto));
    }

    @Test
    void updateSettings(SeededRng random) throws NoSuchEntityException {
        var me = random.<UserDetailsDto>get();
        var dto = random.<AccountSettingsDto>get();
        var out = random.<FullAccountDto>get();
        when(accountService.updateSettings(me.getId(), dto)).thenReturn(out);
        assertThat(fixture.updateSettings(me, dto)).isEqualTo(ResponseEntity.ok(out));
    }

}
