package io.github.naomimyselfandi.xanaduwars.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Log all invocations of the annotated method.
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {

    /// A description of the action this endpoint performs. This should be
    /// human-readable, and does not need to be unique.
    String value();

    /// The action to take when the method is called without authentication. For
    /// example, this can occur when an audited method is called by a
    /// [scheduled task][org.springframework.scheduling.annotation.Scheduled].
    MissingAuthPolicy whenNotAuthenticated() default MissingAuthPolicy.WRITE_INCOMPLETE_ENTRY_AND_WARN;

    /// An action to take when a method is called without authentication. For
    /// example, this can occur when an audited method is called by a
    /// [scheduled task][org.springframework.scheduling.annotation.Scheduled].
    enum MissingAuthPolicy {
        WRITE_INCOMPLETE_ENTRY_AND_WARN,
        WRITE_INCOMPLETE_ENTRY,
        WRITE_NOTHING
    }

}
