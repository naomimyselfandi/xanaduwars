package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/// An email address.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record EmailAddress(@JsonValue @Column(name = "email_address") @Email @NotNull String emailAddress) {

    @Override
    @SuppressWarnings("DeconstructionCanBeUsed") // It confuses the coverage report
    public boolean equals(@Nullable Object obj) {
        return obj instanceof EmailAddress that && lower(this.emailAddress).equals(lower(that.emailAddress));
    }

    @Override
    public int hashCode() {
        return lower(emailAddress).hashCode();
    }

    @Override
    public String toString() {
        return emailAddress;
    }

    private static String lower(String string) {
        return string.toLowerCase(Locale.ROOT);
    }

}
