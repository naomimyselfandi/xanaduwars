package io.github.naomimyselfandi.xanaduwars.core.message;

import io.github.naomimyselfandi.xanaduwars.util.Cleanup;

/// A message bus.
public interface MessageBus {

    /// Evaluate a game query and return its result. The query result may be
    /// cached at the implementation's discretion.
    <T> T evaluate(Query<T> query);

    /// Dispatch a game event. If any queries are cached, the cache is cleared
    /// before the event is dispatched.
    void dispatch(Event event);

    /// Clear the query cache, if applicable.
    void clearQueryCache();

    /// Attach an event listener to this message bus. Whenever this message bus
    /// dispatches an event, the event listener receives it before any rules do.
    /// Execute the returned callback to remove the event listener.
    Cleanup attachEventListener(EventListener listener);

}
