package io.github.naomimyselfandi.xanaduwars.core.message;

import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/// A game rule.
/// @param on The message type this rule handles.
/// @param when A script that restricts the messages this rule handles.
/// @param then A script that defines this rule's effect.
public record Rule(MessageType on, Script when, Script then) {

    /// A game rule.
    /// @param on The message type this rule handles.
    /// @param when A script that restricts the messages this rule handles. If
    /// this isn't given, it defaults to a script that always returns `true`.
    /// @param then A script that defines this rule's effect.
    public Rule(MessageType on, @Nullable Script when, Script then) {
        this.on = on;
        this.when = Objects.requireNonNullElse(when, Script.TRUE);
        this.then = then;
    }

}
