package io.github.naomimyselfandi.xanaduwars.util;

import org.springframework.data.jpa.domain.Specification;

/// A DTO representing filter options for some entity.
public interface FilterDto<T> {

    /// Convert this filter DTO to a specification for use in queries.
    Specification<T> toSpecification();

}
