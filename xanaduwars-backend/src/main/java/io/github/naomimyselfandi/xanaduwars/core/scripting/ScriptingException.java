package io.github.naomimyselfandi.xanaduwars.core.scripting;

import lombok.experimental.StandardException;

/// An exception thrown by a scripting `throw` statement. Scripting exceptions
/// indicate serious errors, like an ordinary [RuntimeException].
@StandardException
public class ScriptingException extends RuntimeException {}
