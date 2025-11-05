package io.github.naomimyselfandi.xanaduwars.security.hateoas;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authorization.method.PreAuthorizeAuthorizationManager;

import static org.assertj.core.api.Assertions.*;

class SecurityAwareAssemblerConfigurationTest {

    @Mock
    private MethodSecurityExpressionHandler handler;

    @Test
    void preAuthorizeAuthorizationManager() {
        assertThat(new SecurityAwareAssemblerConfiguration().preAuthorizeAuthorizationManager(handler))
                .isExactlyInstanceOf(PreAuthorizeAuthorizationManager.class);
    }

}
