package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.lang.Contract;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/// A set of roles, implemented as a bitfield for persistence.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record RoleSet(@Column(name = "roles") int bitfield) implements Serializable {

    /// An empty role set.
    public static RoleSet NONE = new RoleSet(0);

    /// View this role set as a standard Java set.
    @JsonValue
    @Contract("-> new")
    public Set<Role> asCollection() {
        var result = EnumSet.noneOf(Role.class);
        for (var role : Role.values()) {
            if ((bitfield & (1 << role.ordinal())) != 0) {
                result.add(role);
            }
        }
        return result;
    }

    /// Create a role set from a standard Java collection.
    @JsonCreator
    public static RoleSet of(Collection<Role> roles) {
        var bitfield = 0;
        for (var role : roles) {
            bitfield |= (1 << role.ordinal());
        }
        return new RoleSet(bitfield);
    }

    @Override
    public String toString() {
        return asCollection().toString();
    }

}
