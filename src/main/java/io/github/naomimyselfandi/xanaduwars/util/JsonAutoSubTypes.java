package io.github.naomimyselfandi.xanaduwars.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.VisibleForTesting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Consumer;

/// Automatically set up polymorphic deserialization for the annotated type.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonAutoSubTypes {

    /// The configuration logic.
    @VisibleForTesting
    Consumer<ObjectMapper> SUPPORT = JsonAutoSubTypesScanner.POST_CONFIGURER;

}
