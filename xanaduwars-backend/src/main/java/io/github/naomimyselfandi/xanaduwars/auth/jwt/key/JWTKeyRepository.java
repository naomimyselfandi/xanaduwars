package io.github.naomimyselfandi.xanaduwars.auth.jwt.key;

import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTPurpose;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/// A repository for JWT keys.
interface JWTKeyRepository extends CrudRepository<JWTKey, UUID> {

    /// Look up an existing key by ID, validating its purpose and expiry.
    @Query("SELECT j FROM JWTKey j WHERE j.id = ?1 AND j.purpose = ?2 and j.expiry > ?3")
    Optional<JWTKey> findExistingKey(UUID id, JWTPurpose purpose, Instant expiry);

    /// Look up an existing key by its purpose and expiry.
    @Query("SELECT j FROM JWTKey j WHERE j.purpose = ?1 and j.expiry > ?2 ORDER BY j.expiry LIMIT 1")
    Optional<JWTKey> findExistingKey(JWTPurpose purpose, Instant expiry);

    /// Delete all expired keys.
    long deleteByExpiryBefore(Instant expiry);

    /// Acquire a transactional lock for creating JWT keys.
    @Query(value = "SELECT pg_advisory_xact_lock(1670061086389553682)", nativeQuery = true)
    void awaitTransactionalLock();

}
