package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;

import java.lang.annotation.*;

/// Configure the test account in an end-to-end test.
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface WithTestUser {

    /// Specify the roles to grant the test account.
    Role[] roles() default {};

    /// Specify the kind of account to create.
    AccountKind kind() default AccountKind.HUMAN;

}
