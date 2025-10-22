package io.github.naomimyselfandi.xanaduwars.core.message;

/// An event listener.
public interface EventListener {

    /// Notify this listener of an event.
    void receive(Event event);

}
