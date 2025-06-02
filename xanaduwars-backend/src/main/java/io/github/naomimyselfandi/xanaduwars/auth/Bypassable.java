package io.github.naomimyselfandi.xanaduwars.auth;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Allow an access check to be bypassed. If the currently authenticated user
/// has any of the roles given here, the annotated method always returns `true`.
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bypassable {

    /// The role or roles that the access check doesn't apply to.
    Role[] value();

}
