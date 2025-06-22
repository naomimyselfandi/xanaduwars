package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.xanaduwars.account.value.*;

import io.github.naomimyselfandi.xanaduwars.util.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/// A user's account.
@Entity
@Getter
@Setter
public class Account extends AbstractEntity<Account> {

    /// This account's unique username.
    @Embedded
    private @NotNull @Valid Username username;

    /// The canonical form of this account's username. This is used when looking
    /// up accounts by name, and to generalize "uniqueness" to include visually
    /// similar usernames.
    @Embedded
    @Setter(AccessLevel.NONE)
    private @NotNull @Valid CanonicalUsername canonicalUsername;

    /// This accounts's unique email address. Email addresses are
    /// case-insensitive.
    @Embedded
    private @NotNull @Valid EmailAddress emailAddress;

    /// The hash of this user's password.
    @Embedded
    private @NotNull @Valid Password password;

    /// When this account was registered.
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /// The approximate time of this account's last activity.
    private @NotNull Instant lastSeenAt = Instant.EPOCH;

    /// This account's user-configurable settings.
    private @NotNull @Valid AccountSettings settings = new AccountSettings();

    // One boolean per role adds a little boilerplate but makes them trivial to query on.

    /// Whether this account is an admin.
    private boolean admin;

    /// Whether this account is a moderator.
    private boolean moderator;

    /// Whether this account is part of the support team.
    private boolean support;

    /// Whether this account is a tournament judge.
    private boolean judge;

    /// Whether this account is a Xanadu developer or playtester.
    private boolean developer;

    /// Whether the account is a bot. A bot's password is effectively an API key
    /// used with Basic authentication.
    private boolean bot;

    /// Test if this account has a given role. This does *not* automatically
    /// consider implied permissions, which is intentional; we want to be able
    /// to remove an implied permission from an account if we want to.
    public boolean hasRole(Role role) {
        return switch (role) {
            case SUPPORT -> support;
            case MODERATOR -> moderator;
            case JUDGE -> judge;
            case ADMIN -> admin;
            case DEVELOPER -> developer;
            case BOT -> bot;
        };
    }

    /// Grant or revoke a role from this account. If a role which has implied
    /// roles is granted, all of its implied roles are also granted.
    public Account setRole(Role role, boolean grant) {
        var _ = switch (role) {
            case SUPPORT -> support = grant;
            case MODERATOR -> moderator = grant;
            case JUDGE -> judge = grant;
            case ADMIN -> admin = grant;
            case DEVELOPER -> developer = grant;
            case BOT -> bot = grant;
        };
        if (grant) {
            for (var impliedRole : role.getImpliedRoles()) {
                setRole(impliedRole, true);
            }
        }
        return this;
    }

    /// Prepare this account for persistence.
    @PreUpdate
    @PrePersist
    public void prepare() {
        if (username != null) {
            canonicalUsername = username.toCanonicalForm();
        }
    }

}
