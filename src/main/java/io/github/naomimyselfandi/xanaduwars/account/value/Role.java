package io.github.naomimyselfandi.xanaduwars.account.value;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/// A role a user can have.
public enum Role implements GrantedAuthority {

    /// Users who can provide technical support.
    SUPPORT,

    /// Xanadu developers and playtesters.
    DEVELOPER(SUPPORT),

    /// Users who can temporarily suspend users and manage moderation notes.
    MODERATOR,

    /// Users who can observe tournament games.
    JUDGE,

    /// Users with unrestricted access.
    ADMIN(DEVELOPER, MODERATOR, JUDGE),

    /// Bot users. A bot's password is effectively an API key used with Basic
    /// authentication.
    BOT;

    /// Any roles implied by this role. This is not transitive.
    @Getter
    private final Set<Role> impliedRoles;

    Role(Role... impliedRoles) {
        this.impliedRoles = Set.of(impliedRoles);
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }

}
