package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

/// A username.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Username(
        @Pattern(regexp = "^\\S+(\\s\\S+)*$")
        @Pattern(regexp = "^[\\p{L}\\p{N} _\\-!@#$%^+=.]{1,32}$")
        @JsonValue @Column(name = "username") @NotNull String username
) implements Serializable {

    /// Get this username's canonical form. This is the form used when looking
    /// up users by name, both to provide a cleaner experience and to protect
    /// against misleading usernames.
    public CanonicalUsername toCanonicalForm() {
        return new CanonicalUsername(username);
    }

    @Override
    public String toString() {
        return username;
    }

}
