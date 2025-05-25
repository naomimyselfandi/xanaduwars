package io.github.naomimyselfandi.xanaduwars.ext;

import java.lang.annotation.*;

/// Documents an interface as a *convenience mixin*. A convenience mixin
/// provides `default` methods intended to assist in implementing another API,
/// and may define additional helper methods (not necessarily `default`) that
/// those implementations rely on. They are conceptually similar to abstract
/// classes but avoid some of their limitations.
///
/// This annotation is purely documentary, with no effect on runtime behavior.
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ConvenienceMixin {}
