package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.auth.dto.Registration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// A controller used to register new accounts.
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/registration")
public class RegistrationController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody Registration registration) {
        accountService.create(registration.username(), registration.emailAddress(), registration.password());
        return ResponseEntity.noContent().build();
    }

}
