package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.Password;
import io.github.naomimyselfandi.xanaduwars.auth.dto.Registration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// A controller used to register new accounts.
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/registration")
public class RegistrationController {

    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody Registration registration) {
        var username = registration.username();
        var emailAddress = registration.emailAddress();
        var password = new Password(passwordEncoder.encode(registration.password().text()));
        accountService.create(username, emailAddress, password);
        return ResponseEntity.noContent().build();
    }

}
