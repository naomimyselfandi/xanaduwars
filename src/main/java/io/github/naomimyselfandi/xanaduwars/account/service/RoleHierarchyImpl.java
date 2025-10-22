package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
class RoleHierarchyImpl implements RoleHierarchy {

    private static final Set<Role> ADMIN_ROLES = Set.of(Role.values());

    @Override
    public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
            Collection<? extends GrantedAuthority> authorities
    ) {
        return authorities.contains(Role.ADMIN) ? ADMIN_ROLES : authorities;
    }

}
