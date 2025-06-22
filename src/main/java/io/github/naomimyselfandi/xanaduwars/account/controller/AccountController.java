package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.xanaduwars.account.dto.AccountSettingsDto;
import io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.dto.FullAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.audit.Audited;
import io.github.naomimyselfandi.xanaduwars.auth.Authenticated;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/// A controller for working with accounts.
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseAccountDto> getBase(@PathVariable Id<Account> id) throws NoSuchEntityException {
        return ResponseEntity.ok(accountService.get(BaseAccountDto.class, id));
    }

    @GetMapping("/{id}/full")
    @Audited("ACCOUNT_READ_FULL")
    @PreAuthorize("hasRole('SUPPORT')")
    public ResponseEntity<FullAccountDto> getFull(@PathVariable Id<Account> id) throws NoSuchEntityException {
        return ResponseEntity.ok(accountService.get(FullAccountDto.class, id));
    }

    @GetMapping("/me")
    public ResponseEntity<FullAccountDto> getMe(@Authenticated FullAccountDto dto) {
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/me/settings")
    public ResponseEntity<FullAccountDto> updateSettings(
            @Authenticated UserDetailsDto user,
            @RequestBody AccountSettingsDto dto
    ) throws NoSuchEntityException {
        return ResponseEntity.ok(accountService.updateSettings(user.getId(), dto));
    }

}
