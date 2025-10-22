package io.github.naomimyselfandi.xanaduwars.util;

/// An [AutoCloseable] that doesn't throw exceptions.
public interface Cleanup extends AutoCloseable {

    @Override
    void close();

}
