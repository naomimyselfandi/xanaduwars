package io.github.naomimyselfandi.xanaduwars.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FilterDtoServiceImplTest {

    @Mock
    private Specification<Object> specification;

    @InjectMocks
    private FilterDtoServiceImpl fixture;

    @Test
    void toSpecification() {
        assertThat(fixture.toSpecification(() -> specification)).isEqualTo(specification);
    }

}
