package io.github.naomimyselfandi.xanaduwars.util;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Deserialize the annotated element as an immutable map.
@JacksonAnnotationsInside
@Retention(RetentionPolicy.RUNTIME)
@JsonDeserialize(as = ImmutableMap.class)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface JsonImmutableMap {}
