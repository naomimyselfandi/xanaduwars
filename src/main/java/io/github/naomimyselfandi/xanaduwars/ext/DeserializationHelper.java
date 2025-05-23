package io.github.naomimyselfandi.xanaduwars.ext;

import java.lang.annotation.*;

/// Documents an interface as a *deserialization helper*. A deserialization
/// helper provides no API of its own; its only purpose is to hold Jackson
/// polymorphic deserialization annotations.
///
/// This annotation is purely documentary, with no effect on runtime behavior.
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DeserializationHelper {}
