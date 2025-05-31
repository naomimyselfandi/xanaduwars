package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.dto.FullAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountReferenceResolver;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountReference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/// A controller for working with accounts.
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountReferenceResolver accountReferenceResolver;

    @GetMapping("/{ref}")
    @Transactional(readOnly = true)
    public ResponseEntity<BaseAccountDto> getBase(@PathVariable AccountReference ref) {
        return accountReferenceResolver
                .resolve(BaseAccountDto.class, ref)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{ref}/full")
    @Transactional(readOnly = true)
    @PreAuthorize("@accountGuard.isCurrentAccount(#ref) || hasRole('SUPPORT')")
    public ResponseEntity<FullAccountDto> getFull(@PathVariable AccountReference ref) {
        return accountReferenceResolver
                .resolve(FullAccountDto.class, ref)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
