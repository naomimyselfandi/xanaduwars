package io.github.naomimyselfandi.xanaduwars.util;

import org.springframework.data.jpa.domain.Specification;

/// A service that converts filter DTOs to query specifications.
/// @apiNote This interface primarily exists to introduce a testing seam. Call
/// [FilterDto#toSpecification()] directly if this isn't needed.
public interface FilterDtoService {

    /// Convert a filter DTO to a query specification.
    <T> Specification<T> toSpecification(FilterDto<T> filter);

}
