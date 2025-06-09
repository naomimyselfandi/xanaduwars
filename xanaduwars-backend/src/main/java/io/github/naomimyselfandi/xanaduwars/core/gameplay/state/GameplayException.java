package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/// An exception thrown during gameplay. These are typically used to report
/// invalid actions, and so should not be taken as a serious error.
///
/// @apiNote In principle, this should be a checked exception; in practice,
/// doing so introduces an unacceptable amount of noise.
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GameplayException extends RuntimeException {

    /// Create a gameplay exception. This typically reflects an invalid action,
    /// and so should not be taken as a serious error.
    public GameplayException(String message) {
        super(message, null, true, false);
    }

}
