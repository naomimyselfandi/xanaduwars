package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Automatically inject a [TestUser] into the annotated field.
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoUser {

    /// The roles to assign the test user.
    Role[] value() default {};

}
