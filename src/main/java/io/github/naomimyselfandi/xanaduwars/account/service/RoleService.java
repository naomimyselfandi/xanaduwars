package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.util.Id;

import java.util.Collection;

/// A service that manages users' roles.
public interface RoleService {

    /// Check if a user has a role. This respects the role hierarchy; for
    /// example, an administrator has every role, even ones that haven't been
    /// explicitly granted.
    boolean hasRole(UserDetailsDto user, Role role);

    /// Set an account's roles.
    void setRoles(Id<Account> accountId, Collection<Role> roles);

}
