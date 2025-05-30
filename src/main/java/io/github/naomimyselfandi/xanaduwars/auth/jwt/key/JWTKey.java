package io.github.naomimyselfandi.xanaduwars.auth.jwt.key;

import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import java.time.Instant;
import java.util.UUID;

/// A key used to sign JWTs.
@Entity
@Getter
@Setter
@ToString
@Immutable
@Table(name = "jwt_key")
@ExcludeFromCoverageReport // Weird bug
class JWTKey {

    /// Primary key.
    @Id
    @GeneratedValue
    private UUID id;

    /// The base64-encoded form of the private key.
    @ToString.Exclude
    private String encodedSecret;

    /// The purpose of JWTs signed with this key.
    @Enumerated(EnumType.STRING)
    private JWTPurpose purpose;

    /// The timestamp when the key expires.
    private Instant expiry;

}
