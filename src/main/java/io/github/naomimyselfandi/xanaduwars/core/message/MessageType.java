package io.github.naomimyselfandi.xanaduwars.core.message;

import io.github.naomimyselfandi.xanaduwars.core.script.Function;

import java.util.List;

/// A type of message.
///
/// When used as a scripting function, a message type returns an appropriate
/// message, which can then be dispatched or evaluated as normal.
public interface MessageType extends Function {

    /// Get this message type's unique name.
    String name();

    /// Get the names of this message type's properties.
    List<String> properties();

}
