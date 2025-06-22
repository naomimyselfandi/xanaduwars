package io.github.naomimyselfandi.xanaduwars.core.scripting;

/// A game event that does not return a value.
public interface SimpleEvent extends Event<None> {

    @Override
    default None defaultValue() {
        return None.NONE;
    }

}
