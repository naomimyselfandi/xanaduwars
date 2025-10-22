package io.github.naomimyselfandi.xanaduwars.account.value;

import org.springframework.security.core.GrantedAuthority;

/// A role a user can have.
public enum Role implements GrantedAuthority {

    /// Users who can provide technical support.
    SUPPORT,

    /// Xanadu developers and playtesters.
    DEVELOPER,

    /// Users who can temporarily suspend users and manage moderation notes.
    MODERATOR,

    /// Users who can observe tournament games.
    JUDGE,

    /// Users with unrestricted access.
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }

}
