package io.github.naomimyselfandi.xanaduwars.util;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.Hibernate;

import java.util.UUID;

/// An abstract database entity with a UUID primary key. At the application
/// level, the UUID is wrapped in some other type to provide clarity.
@MappedSuperclass
public abstract class AbstractEntity<Self> {

    @Getter
    @EmbeddedId
    private Id<Self> id = new Id<>(UUID.randomUUID());

    public Self setId(Id<Self> id) {
        this.id = id;
        @SuppressWarnings("unchecked")
        var self = (Self) this;
        return self;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractEntity<?> that && this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "%s[id=%s]".formatted(Hibernate.getClass(this).getSimpleName(), id);
    }

}
