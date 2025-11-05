package io.github.naomimyselfandi.xanaduwars.security.hateoas;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authorization.method.PreAuthorizeAuthorizationManager;

@Configuration
class SecurityAwareAssemblerConfiguration {

    @Bean
    PreAuthorizeAuthorizationManager preAuthorizeAuthorizationManager(MethodSecurityExpressionHandler handler) {
        var result = new PreAuthorizeAuthorizationManager();
        result.setExpressionHandler(handler);
        return result;
    }

}
