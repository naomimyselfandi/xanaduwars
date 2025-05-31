package io.github.naomimyselfandi.xanaduwars.ext;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Allow the annotated type to have JSON comments. A comment is simply a field
/// whose name is `$comment`, not an extension to the JSON spec.
@Target(ElementType.TYPE)
@JacksonAnnotationsInside
@JsonIgnoreProperties("$comment")
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonCommentable {}
