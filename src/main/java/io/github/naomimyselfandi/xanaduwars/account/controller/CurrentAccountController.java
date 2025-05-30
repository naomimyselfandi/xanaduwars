package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.xanaduwars.account.dto.FullAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.auth.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// A controller for users to manage their own accounts.
@RestController
@RequiredArgsConstructor
@RequestMapping("/account/me")
public class CurrentAccountController {

    @GetMapping
    public ResponseEntity<FullAccountDto> me(@CurrentAccount Account me) {
        return ResponseEntity.ok(new FullAccountDto(me));
    }

}
