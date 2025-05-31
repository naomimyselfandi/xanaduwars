package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.*;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountReferenceResolver;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountIdReference;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AccountControllerTest {

    @Mock
    private AccountReferenceResolver accountReferenceResolver;

    @InjectMocks
    private AccountController fixture;

    @Test
    void getBase(SeededRng random) {
        var reference = new AccountIdReference(random.nextUUID());
        var dto = new BaseAccountDto();
        dto.setId(random.nextUUID());
        when(accountReferenceResolver.resolve(BaseAccountDto.class, reference)).thenReturn(Optional.of(dto));
        assertThat(fixture.getBase(reference)).isEqualTo(ResponseEntity.ok(dto));
    }

    @Test
    void getBase_WhenTheAccountDoesNotExist_ThenThrows(SeededRng random) {
        var reference = new AccountIdReference(random.nextUUID());
        when(accountReferenceResolver.resolve(BaseAccountDto.class, reference)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.getBase(reference)).isInstanceOfSatisfying(
                ResponseStatusException.class,
                it -> assertThat(it.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void getFull(SeededRng random) {
        var reference = new AccountIdReference(random.nextUUID());
        var dto = new FullAccountDto();
        dto.setId(random.nextUUID());
        dto.setSettings(new AccountSettingsDto());
        when(accountReferenceResolver.resolve(FullAccountDto.class, reference)).thenReturn(Optional.of(dto));
        assertThat(fixture.getFull(reference)).isEqualTo(ResponseEntity.ok(dto));
    }

    @Test
    void getFull_WhenTheAccountDoesNotExist_ThenThrows(SeededRng random) {
        var reference = new AccountIdReference(random.nextUUID());
        when(accountReferenceResolver.resolve(FullAccountDto.class, reference)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> fixture.getFull(reference)).isInstanceOfSatisfying(
                ResponseStatusException.class,
                it -> assertThat(it.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

}
