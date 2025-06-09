package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeanFactoryResolverConfigurationTest {

    @Mock
    private BeanFactory beanFactory;

    @InjectMocks
    private BeanFactoryResolverConfiguration fixture;

    @Test
    void beanFactoryResolver() {
        assertThat(fixture.beanFactoryResolver(beanFactory)).isInstanceOf(BeanFactoryResolver.class);
    }

}
