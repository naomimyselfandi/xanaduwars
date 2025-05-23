package io.github.naomimyselfandi.xanaduwars.ext;

import java.lang.annotation.*;

/// Documents a package as *organizational*. An organization package exists
/// solely for organizational reasons, and does not correspond to a natural
/// API boundary like a typical package does.
///
/// This annotation is purely documentary, with no effect on runtime behavior.
@Documented
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface OrganizationalPackage {}
