package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.service.RoleService;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final RoleService roleService;
    private final RepresentationModelAssembler<BaseAccountDto, EntityModel<BaseAccountDto>> assembler;

    @GetMapping("/{id}")
    public EntityModel<BaseAccountDto> get(@PathVariable Id<Account> id) {
        return assembler.toModel(accountService.getAccount(id, BaseAccountDto.class));
    }

    @GetMapping("/{id}/yourMom")
    public EntityModel<BaseAccountDto> get(
            @PathVariable Id<Account> id,
            RepresentationModelAssembler<BaseAccountDto, EntityModel<BaseAccountDto>> assembler
    ) {
        return assembler.toModel(accountService.getAccount(id, BaseAccountDto.class));
    }

    @PatchMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseAccountDto> updateRoles(@PathVariable Id<Account> id, @RequestBody Set<Role> roles) {
        roleService.setRoles(id, roles);
        return ResponseEntity.ok(accountService.getAccount(id, BaseAccountDto.class));
    }

}
