package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.value.CanonicalUsername;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.RoleSet;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/// A repository for user accounts.
public interface AccountRepository extends JpaRepository<Account, Id<Account>> {

    /// Check if either a username or an email address is unavailable.
    boolean existsByCanonicalUsernameOrEmailAddress(CanonicalUsername canonicalUsername, EmailAddress emailAddress);

    /// Find an account by ID and project it into a DTO.
    @Transactional(readOnly = true)
    <T extends AccountDto> Optional<T> findById(Id<Account> id, Class<T> representation);

    /// Find an account by username and project it into a DTO.
    @Transactional(readOnly = true)
    <T extends AccountDto> Optional<T> findByCanonicalUsername(CanonicalUsername username, Class<T> representation);

    /// Find an account by email address and project it into a DTO.
    @Transactional(readOnly = true)
    <T extends AccountDto> Optional<T> findByEmailAddress(EmailAddress emailAddress, Class<T> representation);

    /// Update an account's "last seen at" timestamp.
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.lastSeenAt = ?2 WHERE a.id = ?1")
    void updateLastSeenAtById(Id<Account> id, Instant lastSeenAt);

    /// Update an account's "remember me" flag.
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.rememberMe = ?2 WHERE a.id = ?1")
    void updateRememberMeById(Id<Account> id, boolean rememberMe);

    /// Update an account's roles.
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.roles = ?2 WHERE a.id = ?1")
    void updateRolesById(Id<Account> id, RoleSet roles);

}
