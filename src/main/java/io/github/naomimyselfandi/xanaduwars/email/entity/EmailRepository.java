package io.github.naomimyselfandi.xanaduwars.email.entity;

import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/// A repository for managing emails.
public interface EmailRepository extends JpaRepository<Email, Id<Email>> {

    /// An email that has been claimed for sending.
    /// @apiNote This record uses basic Java types instead of our preferred
    /// domain-specific types to work around limitations of JPA native queries.
    record ClaimedEmail(UUID id, String address, String content, int attempts) {}

    /// Claim a batch of emails for sending.
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            UPDATE email
            SET status = 'SENDING', status_time = NOW(), status_reason = 'Claimed for sending'
            WHERE id IN (
              SELECT id
              FROM email
              WHERE (status = 'PENDING' AND status_time <= NOW())
                 OR (status = 'SENDING' AND status_time <= NOW() - INTERVAL '15 MINUTES')
              ORDER BY status_time
              LIMIT ?1
              FOR UPDATE SKIP LOCKED
            )
            RETURNING id, address, content, attempts
            """)
    List<ClaimedEmail> claimEmails(int batchSize);

    /// Record an attempt to send an email.
    @Modifying
    @Transactional
    @Query("""
            UPDATE Email e
            SET e.status = ?2,
                e.statusReason = ?3,
                e.statusTime = ?4,
                e.attempts = e.attempts + 1
            WHERE e.id = ?1
            """)
    void recordSendingAttempt(Id<Email> id, Email.Status status, String statusReason, Instant statusTime);

}
