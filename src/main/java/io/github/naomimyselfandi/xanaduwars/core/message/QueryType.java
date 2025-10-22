package io.github.naomimyselfandi.xanaduwars.core.message;

import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import org.jetbrains.annotations.Nullable;

/// A type of query.
public interface QueryType<T> extends MessageType {

    /// Calculate the default value of a query of this type.
    T defaultValue(ScriptRuntime runtime,  @Nullable Object... arguments);

}
