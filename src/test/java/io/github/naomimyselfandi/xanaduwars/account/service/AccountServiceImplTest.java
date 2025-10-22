package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AccountServiceImplTest {

    private interface Helper extends AccountDto {}

    @Mock
    private Helper helper;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl fixture;

    @Test
    void getAccount(SeededRng random) {
        var id = random.<Id<Account>>get();
        when(accountRepository.findById(id, Helper.class)).thenReturn(Optional.of(helper));
        assertThat(fixture.getAccount(id, Helper.class)).isEqualTo(helper);
    }

    @Test
    void getAccount_WhenTheAccountDoesNotExist_ThenThrows(SeededRng random) {
        var id = random.<Id<Account>>get();
        assertThatThrownBy(() -> fixture.getAccount(id, Helper.class))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(new NotFoundException(id).getMessage());
    }

}
