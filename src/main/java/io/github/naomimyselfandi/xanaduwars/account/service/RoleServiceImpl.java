package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.account.value.RoleSet;
import io.github.naomimyselfandi.xanaduwars.security.Audited;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
class RoleServiceImpl implements RoleService {

    private final AccountRepository accountRepository;

    @Override
    public boolean hasRole(UserDetailsDto user, Role role) {
        var roles = user.roles().asCollection();
        return roles.contains(role) || roles.contains(Role.ADMIN);
    }

    @Override
    @Transactional
    @Audited("CHANGE_ROLES")
    @PreAuthorize("hasRole('ADMIN')")
    public void setRoles(Id<Account> accountId, Collection<Role> roles) {
        accountRepository.updateRolesById(accountId, RoleSet.of(roles));
    }

}
