package io.github.naomimyselfandi.xanaduwars.core.message;

import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.util.Cleanup;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/// A helper for implementing message buses.
public abstract class AbstractMessageBus implements MessageBus, ScriptRuntime {

    private final Map<Query<?>, Object> queryCache = new HashMap<>();
    private final List<EventListener> eventListeners = new ArrayList<>();

    @Override
    public <T> T evaluate(Query<T> query) {
        // arbitrary cache key - we can't use computeIfAbsent because of CMEs
        var result = queryCache.get(query);
        if (result == null) {
            result = evaluate(query.defaultValue(this), query);
            queryCache.put(query, result);
        }
        @SuppressWarnings("unchecked")
        var t = (T) result;
        return t;
    }

    @Override
    public void dispatch(Event event) {
        queryCache.clear();
        for (var eventListener : eventListeners) {
            eventListener.receive(event);
        }
        evaluate(null, event);
    }

    @Override
    public void clearQueryCache() {
        queryCache.clear();
    }

    @Override
    public Cleanup attachEventListener(EventListener listener) {
        eventListeners.add(listener);
        return () -> eventListeners.remove(listener);
    }

    /// Get any global rules.
    protected abstract Stream<Rule> getGlobalRules();

    @Contract("!null, _ -> !null")
    private @Nullable Object evaluate(@Nullable Object value, Message message) {
        var messageType = message.type();
        var messageArguments = message.arguments();
        var resultType = message instanceof Query<?> query ? query.resultType() : null;
        var map = getArgumentMap(messageType, messageArguments);
        var rules = getRules(messageType, messageArguments);
        for (var rule : rules) {
            map.put("value", value);
            if (rule.when().executeNotNull(this, map)) {
                var intermediate = rule.then().execute(this, map, resultType);
                if (intermediate != null) {
                    value = intermediate;
                }
            }
        }
        return value;
    }

    private static Map<String, Object> getArgumentMap(MessageType messageType, List<Object> messageArguments) {
        var count = messageArguments.size();
        var properties = messageType.properties();
        if (properties.size() != count) {
            var format = "A message of type '%s' needs %d arguments, not %d.";
            var message = format.formatted(messageType.name(), properties.size(), count);
            throw new IllegalArgumentException(message);
        }
        var map = new HashMap<String, Object>(count + 1);
        for (var i = 0; i < count; i++) {
            map.put(properties.get(i), messageArguments.get(i));
        }
        return map;
    }

    private List<Rule> getRules(MessageType type, List<Object> messageArguments) {
        var rules = getGlobalRules();
        if (!messageArguments.isEmpty() && messageArguments.getFirst() instanceof ContextualRuleSource subject) {
            rules = Stream.concat(rules, subject.getContextualRules());
        }
        return rules.filter(it -> it.on().equals(type)).toList();
    }

}
