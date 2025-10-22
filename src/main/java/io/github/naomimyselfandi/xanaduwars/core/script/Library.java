package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;

/// An object which provides scripting objects by name.
public interface Library {

    /// Look up one of this library's members by name.
    @Nullable Object lookup(String name);

}
