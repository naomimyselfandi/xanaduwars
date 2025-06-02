package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.xanaduwars.account.value.AccountId;
import io.github.naomimyselfandi.xanaduwars.account.value.CanonicalUsername;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/// A repository for user accounts.
public interface AccountRepository extends CrudRepository<Account, AccountId> {

    /// Find an account by the canonical form of its username. This method
    /// should be used for all username-based lookups to ensure consistent
    /// matching behavior.
    Optional<Account> findByCanonicalUsername(CanonicalUsername canonicalUsername);

    /// Find an account by its email address. Email addresses are always
    /// case-insensitive.
    Optional<Account> findByEmailAddress(EmailAddress emailAddress);

    /// Check if a username or email address is already in use.
    boolean existsByCanonicalUsernameOrEmailAddress(CanonicalUsername canonicalUsername, EmailAddress emailAddress);

}
