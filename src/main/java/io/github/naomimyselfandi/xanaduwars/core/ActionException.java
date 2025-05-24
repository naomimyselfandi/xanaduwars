package io.github.naomimyselfandi.xanaduwars.core;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

/// An exception thrown to indicate an invalid action or group of actions.
@Value
@EqualsAndHashCode(callSuper = false)
public class ActionException extends Exception {

    /// The action that caused this exception. If this is `null`, this exception
    /// indicates that the issue involved multiple actions; this might happen if
    /// an element tries to execute too many actions at once.
    @Nullable Action<?, ?> action;

    /// The reason why the action or group of actions is invalid. This is
    /// usually a rule or a resource, indicating that a game rule would be
    /// violated or a cost couldn't be paid, respectively. It may also be
    /// `null`,  indicating a [structural issue][Action#test(Object, Element)].
    @Nullable Object reason;

}
