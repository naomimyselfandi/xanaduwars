package io.github.naomimyselfandi.xanaduwars.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Audit all invocations of the annotated method.
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {

    /// A description of the action this endpoint performs. This should be
    /// human-readable, and does not need to be unique.
    String value();

    /// The action to take when the method is called in a non-HTTP context.
    MissingRequestPolicy whenNoRequest() default MissingRequestPolicy.WRITE_INCOMPLETE_ENTRY_AND_WARN;

    /// An action to take when the method is called in a non-HTTP context.
    enum MissingRequestPolicy {
        WRITE_INCOMPLETE_ENTRY_AND_WARN,
        WRITE_INCOMPLETE_ENTRY,
        SKIP
    }

}
