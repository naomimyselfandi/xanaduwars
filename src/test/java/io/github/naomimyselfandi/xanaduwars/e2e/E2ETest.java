package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/// A specification of the user to use for an end-to-end test. If this
/// annotation is present multiple times, the test is run once per appearance,
/// or multiple times if a [repetition count][#repetitions()] is given.
@TestTemplate
@Target(ElementType.METHOD)
@Repeatable(E2ETest.E2ETests.class)
@ExtendWith(E2ETestExtension.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface E2ETest {

    /// Specify whether a test account should be used.
    boolean value() default true;

    /// Specify the roles to grant the test account.
    Role[] roles() default {};

    /// How many times to use a test account matching this specification.
    int repetitions() default 1;

    /// Additional information to use in the test.
    String payload() default "";

    /// Repeatable annotation container.
    @TestTemplate
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface E2ETests {

        /// Repeatable annotation container.
        E2ETest[] value();

    }

}
