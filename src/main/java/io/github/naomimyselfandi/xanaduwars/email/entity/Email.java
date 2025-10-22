package io.github.naomimyselfandi.xanaduwars.email.entity;

import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.email.value.EmailContent;
import io.github.naomimyselfandi.xanaduwars.util.AbstractEntity;
import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Getter
@Setter
@NotCovered // Confusing false positive
public class Email extends AbstractEntity<Email> {

    /// An email's status.
    public enum Status {

        /// The email has not been sent yet. Alternatively, a previous attempt
        /// to send the email failed, but the failure may be recoverable.
        PENDING,

        /// The email has been claimed for sending.
        SENDING,

        /// The email has been successfully sent.
        SENT,

        /// Sending the email has failed, and will not be retried.
        FAILED,

    }

    /// The address the email should be delivered to.
    @Embedded
    @AttributeOverride(name = "emailAddress", column = @Column(name = "address"))
    private @NotNull @Valid EmailAddress address;

    /// This email's content.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSONB")
    @SuppressWarnings("com.intellij.jpb.UnsupportedTypeWithoutConverterInspection")
    private @NotNull @Valid EmailContent content;

    /// How many attempts have been made to deliver this email.
    private @PositiveOrZero int attempts;

    /// The email's current status.
    @Enumerated(EnumType.STRING)
    private @NotNull Status status = Status.PENDING;

    /// When this email should be sent, was sent, or was claimed for sending.
    private @NotNull Instant statusTime;

    /// Further human-readable information about the email's status. This is
    /// typically a failure message used in debugging.
    private @NotNull String statusReason = "";

}
