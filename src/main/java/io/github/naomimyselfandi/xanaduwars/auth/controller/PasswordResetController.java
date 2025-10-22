package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.auth.service.PasswordResetService;
import io.github.naomimyselfandi.xanaduwars.auth.value.PasswordResetToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/// A controller for resetting one's password.
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/passwordReset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping
    public ResponseEntity<Void> createResetToken(@RequestParam EmailAddress emailAddress) {
        passwordResetService.createResetTokenForUserRequest(emailAddress);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{token}")
    public ResponseEntity<UserDetailsDto> validateResetToken(@PathVariable PasswordResetToken token) {
        return passwordResetService
                .validateResetToken(token)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{token}")
    public ResponseEntity<Void> useResetToken(
            @PathVariable PasswordResetToken token,
            @RequestBody PasswordResetRequest request
    ) {
        passwordResetService.resetPassword(token, request.password());
        return ResponseEntity.noContent().build();
    }

}
