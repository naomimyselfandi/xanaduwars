package io.github.naomimyselfandi.xanaduwars.security.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MethodSecurityConfigurationTest {

    @Mock
    private RoleHierarchy roleHierarchy;

    @Mock
    private ApplicationContext applicationContext;

    private final MethodSecurityConfiguration fixture = new MethodSecurityConfiguration();

    @Test
    void methodSecurityExpressionHandler() {
        var handler = fixture.methodSecurityExpressionHandler(roleHierarchy, applicationContext);
        assertThat(handler).isExactlyInstanceOf(DefaultMethodSecurityExpressionHandler.class);
    }

}
