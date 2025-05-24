package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import java.io.Serializable;

/// A wrapper around a string.
public interface StringWrapper extends Serializable {

    /// The string wrapped by this object.
    @Override
    String toString();

}
