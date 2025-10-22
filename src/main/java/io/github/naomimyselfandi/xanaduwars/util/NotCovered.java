package io.github.naomimyselfandi.xanaduwars.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// The annotated element is excluded from the coverage report. This annotation
/// is typically used to work around a bug involving IntelliJ's coverage report
/// and Mockito: mocking a type with non-abstract methods (usually an interface
/// with default methods) causes it to report artificially low branch coverage.
/// These methods must still be tested! More formally, if this annotation were
/// removed and the coverage report bug fixed, the report should indicate 100%
/// line and branch coverage for the entire project.
///
/// This annotation may also be used on trivial enum and record types.
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface NotCovered {}
