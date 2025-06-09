package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;

@Configuration
class BeanFactoryResolverConfiguration {

    @Bean
    BeanResolver beanFactoryResolver(BeanFactory beanFactory) {
        return new BeanFactoryResolver(beanFactory);
    }

}
