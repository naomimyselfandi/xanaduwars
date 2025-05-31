package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountIdReference;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountReference;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AccountGuardImplTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AccountGuardImpl fixture;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isCurrentUser(boolean expected, SeededRng random) {
        var id = random.nextUUID();
        var account = new UserDetailsDto();
        account.setId(expected ? id : random.nextUUID());
        account.setAuthorities(List.of());
        when(authService.tryGet()).thenReturn(Optional.of(account));
        assertThat(fixture.isCurrentAccount(new AccountIdReference(id))).isEqualTo(expected);
    }

    @Test
    void isCurrentUser_WhenTheSpecialCurrentUserReferenceIsGiven_ThenTrue() {
        assertThat(fixture.isCurrentAccount(AccountReference.CURRENT_ACCOUNT)).isTrue();
    }

}
