package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.xanaduwars.account.value.*;
import io.github.naomimyselfandi.xanaduwars.util.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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

    /// This user's roles.
    @Embedded
    private @NotNull @Valid RoleSet roles = RoleSet.NONE;

    /// When this account was registered.
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private @NotNull Instant createdAt;

    /// The approximate time of this account's last activity.
    private @NotNull Instant lastSeenAt = Instant.EPOCH;

    /// Whether this account has enabled "remember me" mode.
    private boolean rememberMe;

    /// Prepare this account for persistence.
    @PreUpdate
    @PrePersist
    public void prepare() {
        // null check so any failures happen during validation
        if (username != null) {
            canonicalUsername = username.toCanonicalForm();
        }
    }

}
