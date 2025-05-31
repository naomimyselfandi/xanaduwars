package io.github.naomimyselfandi.xanaduwars;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @Mock
    private HandlerMethodArgumentResolver a, b, c, x, y, z;

    private WebConfig fixture;

    @BeforeEach
    void setup() {
        fixture = new WebConfig(List.of(a, b, c));
    }

    @Test
    void addArgumentResolvers() {
        var list = new ArrayList<>(List.of(x, y, z));
        fixture.addArgumentResolvers(list);
        assertThat(list).containsExactly(x, y, z, a, b, c);
    }

}
