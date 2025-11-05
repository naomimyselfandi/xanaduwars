package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.service.RoleService;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AccountControllerTest {

    @Mock
    private EntityModel<BaseAccountDto> entityModel;

    @Mock
    private AccountService accountService;

    @Mock
    private RoleService roleService;

    @Mock
    private RepresentationModelAssembler<BaseAccountDto, EntityModel<BaseAccountDto>> assembler;

    @InjectMocks
    private AccountController fixture;

    @Test
    void get(SeededRng random) {
        var id = random.<Id<Account>>get();
        var dto = random.<BaseAccountDto>get();
        when(assembler.toModel(dto)).thenReturn(entityModel);
        when(accountService.getAccount(id, BaseAccountDto.class)).thenReturn(dto);
        assertThat(fixture.get(id)).isEqualTo(entityModel);
    }

    @Test
    void updateRoles(SeededRng random) {
        var id = random.<Id<Account>>get();
        var role1 = random.<Role>get();
        var role2 = random.not(role1);
        var roles = Set.of(role1, role2);
        var dto = random.<BaseAccountDto>get();
        when(accountService.getAccount(id, BaseAccountDto.class)).then(_ -> {
           verify(roleService).setRoles(id, roles);
           return dto;
        });
        assertThat(fixture.updateRoles(id, roles)).isEqualTo(ResponseEntity.ok(dto));
    }

}
