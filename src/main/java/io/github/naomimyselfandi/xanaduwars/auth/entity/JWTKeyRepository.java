package io.github.naomimyselfandi.xanaduwars.auth.entity;

import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;

/// A repository for JWT keys.
public interface JWTKeyRepository extends CrudRepository<JWTKey, Id<JWTKey>> {

    /// Look up an existing key by ID, validating its purpose and expiry.
    @Query("SELECT j FROM JWTKey j WHERE j.id = ?1 AND j.purpose = ?2 and j.expiry > ?3")
    Optional<JWTKey> findExistingKey(Id<JWTKey> id, JWTPurpose purpose, Instant expiry);

    /// Look up an existing key by its purpose and expiry.
    @Query("SELECT j FROM JWTKey j WHERE j.purpose = ?1 and j.expiry > ?2 ORDER BY j.expiry LIMIT 1")
    Optional<JWTKey> findExistingKey(JWTPurpose purpose, Instant expiry);

    /// Delete all expired keys.
    void deleteByExpiryBefore(Instant expiry);

    /// Acquire a transactional lock for creating JWT keys.
    @Query(value = "SELECT pg_advisory_xact_lock(1670061086389553682)", nativeQuery = true)
    void awaitTransactionalLock();

}
