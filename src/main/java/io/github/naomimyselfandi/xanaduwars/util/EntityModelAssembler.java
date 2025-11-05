package io.github.naomimyselfandi.xanaduwars.util;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

/// A representation model assembler that produces entity models. This interface
/// serves a strictly ergonomic function: using [RepresentationModelAssembler]
/// directly tends to create inconveniently long types.
@FunctionalInterface
public interface EntityModelAssembler<T> extends RepresentationModelAssembler<T, EntityModel<T>> {}
