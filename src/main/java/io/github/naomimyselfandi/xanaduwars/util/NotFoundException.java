package io.github.naomimyselfandi.xanaduwars.util;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/// An exception indicating that an entity couldn't be found.
@StandardException
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    /// Construct an exception indicating an unknown entity ID.
    public NotFoundException(Id<?> id, Class<?> entityType) {
        this("No %s with ID %s.".formatted(entityType.getSimpleName(), id));
    }

    /// Construct an exception indicating an unknown entity ID.
    ///
    /// Do not pass any varargs to this method. The varargs parameter is used to
    /// infer the type of entity and reflect it in the error message.
    @SafeVarargs
    public <T> NotFoundException(Id<T> id, T... carrier) {
        this(id, carrier.getClass().getComponentType());
    }

}
