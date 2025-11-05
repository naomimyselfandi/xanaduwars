package io.github.naomimyselfandi.xanaduwars.security.hateoas;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.security.service.AuditService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Cleanup;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SecurityAwareAssemblerTest {

    private record Something(int id) {}

    @Mock
    private Authentication authentication;

    @Mock
    private AuditService auditService;

    @Mock
    private Cleanup cleanup;

    @Mock
    private AuthorizationManager<MethodInvocation> authorizationManager;

    @SuppressWarnings("unused")
    private static class Fixture extends SecurityAwareAssembler<Something> {

        @Link
        public Object foo(TestController controller, Something something) {
            return controller.getFoo(something.id);
        }

        @Link
        public Object bar(TestController controller, Something something) {
            return controller.getBar(something.id);
        }

        @Link
        public Object baz(TestController controller, Something something) {
            return controller.getBaz(something.id);
        }

        @Link
        public Void bat(TestController controller, Something something) {
            return null;
        }

    }

    private Fixture fixture;

    @BeforeEach
    void setup() {
        fixture = new Fixture();
        fixture.auditService = auditService;
        fixture.authorizationManager = authorizationManager;
    }

    @Test
    @SuppressWarnings({"resource", "ResultOfMethodCallIgnored"})
    void addLinks(SeededRng random) {
        when(auditService.suppress()).thenReturn(cleanup);
        var id = random.nextInt();
        var something = new Something(id);
        var model = EntityModel.of(something);
        fixture.entityModelStrategy = content -> {
            assertThat(content).isEqualTo(something);
            return model;
        };
        when(authorizationManager.authorize(any(), any())).then(invocation -> {
            var auth = invocation.<Supplier<Authentication>>getArgument(0);
            try {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                assertThat(auth.get()).isEqualTo(authentication);
            } finally {
                SecurityContextHolder.clearContext();
            }
            var methodInvocation = invocation.<MethodInvocation>getArgument(1);
            var method = methodInvocation.getMethod();
            assertThat(methodInvocation.getThis()).isInstanceOf(TestController.class);
            assertThat(methodInvocation.getArguments()).singleElement().isEqualTo(id);
            return switch (method.getName()) {
                case "getFoo" -> new AuthorizationDecision(true);
                case "getBar" -> new AuthorizationDecision(false);
                case "getBaz" -> null;
                default -> fail("Unexpected method " + method);
            };
        });
        assertThat(fixture.toModel(something)).isSameAs(model);
        assertThat(model).isExactlyInstanceOf(EntityModel.class);
        assertThat(model.getLinks()).hasSize(2);
        assertThat(model.getLink("foo"))
                .hasValueSatisfying(it -> assertThat(it.getHref()).isEqualTo("/test/ing/foo/" + id));
        assertThat(model.getLink("baz"))
                .hasValueSatisfying(it -> assertThat(it.getHref()).isEqualTo("/test/ing/baz/" + id));
        var inOrder = inOrder(auditService, authorizationManager, cleanup);
        inOrder.verify(auditService).suppress();
        inOrder.verify(authorizationManager, times(3)).authorize(any(), any());
        inOrder.verify(cleanup).close();
        verifyNoMoreInteractions(authorizationManager);
    }

}
