package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.xanaduwars.account.value.*;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/// A user's account.
@Entity
@Getter
@Setter
@DiscriminatorColumn(name = "kind")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Account {

    /// Primary key.
    @Id
    @GeneratedValue
    private UUID id;

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

    /// The hash of this account's password or API key.
    public abstract Hash authenticationSecret();

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
        };
        if (grant) {
            for (var impliedRole : role.impliedRoles()) {
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
            canonicalUsername = username.canonicalForm();
        }
    }

    @Override
    public String toString() {
        return "Account[id=%s, username=%s]".formatted(id, username);
    }

}
