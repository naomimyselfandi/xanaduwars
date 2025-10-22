package io.github.naomimyselfandi.xanaduwars.security.entity;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.util.AbstractEntity;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

/// An entry in the audit log.
@Getter
@Setter
@Entity
@FieldNameConstants
@Table(name = "audit_log")
@NotCovered// Weird false positive
public class AuditLog extends AbstractEntity<AuditLog> {

    /// The timestamp when the action was taken.
    @CreationTimestamp
    private Instant timestamp;

    /// The ID of the account being audited.
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "account_id"))
    private @Nullable Id<Account> accountId;

    /// The name of the account being audited.
    private @Nullable String username;

    /// The HTTP method of the request that created this entry.
    private @Nullable String httpMethod;

    /// The HTTP path of the request that created this entry.
    private @Nullable String httpPath;

    /// The HTTP query of the request that created this entry.
    private @Nullable String httpQuery;

    /// The HTTP body of the request that created this entry.
    private @Nullable String httpBody;

    /// A human-readable description of the action that was taken.
    private @NotNull String action;

    /// The name of the class whose method created this entry.
    private @Nullable String sourceClass;

    /// The name of the method that created this entry.
    private @Nullable String sourceMethod;

}
