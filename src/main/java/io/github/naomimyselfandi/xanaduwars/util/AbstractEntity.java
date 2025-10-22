package io.github.naomimyselfandi.xanaduwars.util;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.Hibernate;

import java.util.UUID;

/// An abstract database entity with a UUID primary key. At the application
/// level, the UUID is wrapped in a simple record to provide clarity.
@MappedSuperclass
public abstract class AbstractEntity<Self> {

    /// This entity's unique ID.
    @Getter
    @EmbeddedId
    private Id<Self> id = new Id<>(UUID.randomUUID());

    /// Set this entity's unique ID. Using this method should be very rare.
    public Self setId(Id<Self> id) {
        this.id = id;
        @SuppressWarnings("unchecked")
        var self = (Self) this;
        return self;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractEntity<?> that && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "%s[id=%s]".formatted(Hibernate.getClass(this).getSimpleName(), getId());
    }

}
