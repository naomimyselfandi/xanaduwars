package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.service.RoleService;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
class AccountController {

    private final AccountService accountService;
    private final RoleService roleService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseAccountDto> get(@PathVariable Id<Account> id) {
        return ResponseEntity.ok(accountService.getAccount(id, BaseAccountDto.class));
    }

    @PatchMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseAccountDto> updateRoles(@PathVariable Id<Account> id, @RequestBody Set<Role> roles) {
        roleService.setRoles(id, roles);
        return ResponseEntity.ok(accountService.getAccount(id, BaseAccountDto.class));
    }

}
