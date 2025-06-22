package io.github.naomimyselfandi.xanaduwars.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
class FilterDtoServiceImpl implements FilterDtoService {

    @Override
    public <T> Specification<T> toSpecification(FilterDto<T> filter) {
        return filter.toSpecification();
    }

}
