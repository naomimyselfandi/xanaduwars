package io.github.naomimyselfandi.xanaduwars.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
class WebMvcConfigurerImpl implements WebMvcConfigurer {

    private final List<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers;

    private final List<HandlerInterceptor> handlerInterceptors;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(handlerMethodArgumentResolvers);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        handlerInterceptors.forEach(registry::addInterceptor);
    }

}
