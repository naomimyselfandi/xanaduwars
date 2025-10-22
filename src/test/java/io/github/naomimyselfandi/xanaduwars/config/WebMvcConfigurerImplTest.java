package io.github.naomimyselfandi.xanaduwars.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebMvcConfigurerImplTest {

    @Mock
    private HandlerMethodArgumentResolver foo, bar, spam, eggs;

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Mock
    private HandlerInterceptor interceptor;

    private WebMvcConfigurerImpl fixture;

    @BeforeEach
    void setup() {
        fixture = new WebMvcConfigurerImpl(List.of(spam, eggs), List.of(interceptor));
    }

    @Test
    void addArgumentResolvers() {
        var input = new ArrayList<>(List.of(foo, bar));
        fixture.addArgumentResolvers(input);
        assertThat(input).containsExactly(foo, bar, spam, eggs);
    }

    @Test
    void addInterceptors() {
        fixture.addInterceptors(interceptorRegistry);
        verify(interceptorRegistry).addInterceptor(interceptor);
    }

}
