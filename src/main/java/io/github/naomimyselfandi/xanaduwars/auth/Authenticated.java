package io.github.naomimyselfandi.xanaduwars.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Satisfy the annotated parameter through the currently authenticated user.
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {

    /// Whether an authenticated user is required. If this is `false` and the
    /// endpoint is invoked without an authenticated user, the parameter will
    /// be satisfied with `null` instead.
    boolean required() default true;

}
