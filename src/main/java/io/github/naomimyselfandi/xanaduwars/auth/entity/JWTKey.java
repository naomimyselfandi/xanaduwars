package io.github.naomimyselfandi.xanaduwars.auth.entity;

import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.util.AbstractEntity;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

/// A key used to sign JWTs.
@Entity
@Getter
@Setter
@Immutable
@Table(name = "jwt_key")
@NotCovered // Weird false positive
public class JWTKey extends AbstractEntity<JWTKey> {

    /// The base64-encoded form of the private key.
    private String encodedSecret;

    /// The purpose of JWTs signed with this key.
    @Enumerated(EnumType.STRING)
    private JWTPurpose purpose;

    /// The timestamp when the key expires.
    private Instant expiry;

}
