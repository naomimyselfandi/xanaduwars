package io.github.naomimyselfandi.xanaduwars.security;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Allow an access check to be bypassed. If this method is called while the
/// currently authenticated user has any of the roles given here, any result
/// that indicates access is denied is replaced:
///
/// - If the return value is `false`, it is replaced with `true`.
/// - If an exception is thrown, and the exception's class is annotated with
///   `@ResponseStatus(HttpStatus.FORBIDDEN)`, it is caught and ignored.
///
/// If either case holds, an audit record is generated to record the bypass. If
/// the currently authenticated user does not have any of the given roles, or no
/// user is authenticated, this annotation has no effect whatsoever.
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bypassable {

    /// The role or roles that can bypass the access check.
    Role[] value();

}
