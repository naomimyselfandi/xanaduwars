package io.github.naomimyselfandi.xanaduwars.security.hateoas;

import io.github.naomimyselfandi.xanaduwars.security.service.AuditService;
import io.github.naomimyselfandi.xanaduwars.util.EntityModelAssembler;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

/// A HATEOAS assembler that automatically applies security checks. By default,
/// [EntityModel#of] is used to create the entity model; this may be overridden
/// by defining an [EntityModelStrategy&lt;T&gt;][EntityModelStrategy] bean.
public abstract class SecurityAwareAssembler<T> implements EntityModelAssembler<T> {

    /// The annotated method describes a HATEOAS link. The link's target is
    /// specified by calling a method on a controller; this is analogous to
    /// the reflection offered by Spring's [WebMvcLinkBuilder], except that
    /// it automatically checks any access rules defined on the method, and
    /// the link is only added if the access rules succeed. The method must
    /// accept two parameters: a controller of any type, and an instance of
    /// `T`. It may call a method on the controller and return its result,
    /// or return `null` to do nothing.
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface Link {}

    private static final Supplier<Authentication> AUTH = () -> SecurityContextHolder.getContext().getAuthentication();

    @Autowired
    AuditService auditService;

    @Autowired
    AuthorizationManager<MethodInvocation> authorizationManager;

    @Autowired(required = false)
    EntityModelStrategy<T> entityModelStrategy = EntityModel::of;

    /// Convert an entity into a representation model.
    @Override
    public EntityModel<T> toModel(T entity) {
        var model = entityModelStrategy.createModel(entity);
        addLinks(entity, model);
        return model;
    }

    private void addLinks(T entity, EntityModel<T> model) {
        try (var _ = auditService.suppress()) {
            for (var method : getClass().getMethods()) {
                if (method.isAnnotationPresent(Link.class)) {
                    method.setAccessible(true);
                    maybeAddLink(entity, model, method);
                }
            }
        }
    }

    private void maybeAddLink(T entity, EntityModel<T> resource, Method method) {
        var proxy = DummyInvocationUtils.methodOn(method.getParameterTypes()[0]);
        var query = ReflectionUtils.invokeMethod(method, this, proxy, entity);
        if (query != null) {
            var holder = Objects.requireNonNull(DummyInvocationUtils.getLastInvocationAware(query));
            var rawInvocation = holder.getLastInvocation();
            var invocation = new SimpleMethodInvocation(proxy, rawInvocation.getMethod(), rawInvocation.getArguments());
            var decision = authorizationManager.authorize(AUTH, invocation);
            if (decision == null || decision.isGranted()) {
                resource.add(WebMvcLinkBuilder.linkTo(query).withRel(method.getName()));
            }
        }
    }

}
