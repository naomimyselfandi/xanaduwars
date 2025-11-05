package io.github.naomimyselfandi.xanaduwars.security.hateoas;

import org.springframework.hateoas.EntityModel;

/// A strategy for constructing an entity model. For some type `T`, if a bean
/// of type `EntityModelStrategy<T>` is defined, any [SecurityAwareAssembler]
/// with the same type argument will pick it up automatically.
public interface EntityModelStrategy<T> {

    /// Create an entity model.
    EntityModel<T> createModel(T content);

}
