package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Locale;

/// An email address.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record EmailAddress(@JsonValue @Column(name = "email_address") @Email @NotNull String emailAddress)
        implements Serializable {

    /// Construct an email address. It is automatically converted to lower case.
    public EmailAddress(@Email String emailAddress) {
        this.emailAddress = emailAddress.toLowerCase(Locale.ROOT);
    }

    @Override
    @SuppressWarnings("DeconstructionCanBeUsed") // It confuses the coverage report
    public boolean equals(@Nullable Object obj) {
        return obj instanceof EmailAddress that && this.emailAddress.equals(that.emailAddress);
    }

    @Override
    public int hashCode() {
        return emailAddress.hashCode();
    }

    @Override
    public String toString() {
        return emailAddress;
    }

}
