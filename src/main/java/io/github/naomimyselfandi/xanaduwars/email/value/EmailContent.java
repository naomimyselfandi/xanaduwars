package io.github.naomimyselfandi.xanaduwars.email.value;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/// The content of an email. Each implementation of this interface is associated
/// with a template which is expanded with an instance of that implementation to
/// produce the subject line and body.
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@type")
public sealed interface EmailContent permits ActivationContent, PasswordResetContent {}
