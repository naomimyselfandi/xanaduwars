package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.auth.dto.Registration;
import io.github.naomimyselfandi.xanaduwars.auth.service.HashService;
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

    private final HashService hashService;
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody Registration registration) {
        var username = registration.username();
        var emailAddress = registration.emailAddress();
        var password = hashService.hash(registration.password());
        accountService.create(username, emailAddress, password);
        return ResponseEntity.noContent().build();
    }

}
