package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.message.Message;
import io.github.naomimyselfandi.xanaduwars.core.message.MessageType;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// A message implemented as a Java record. Its message type is inferred from
/// the record type's definition.
public interface SimpleMessage extends Message {

    @Override
    default MessageType type() {
        interface Holder {
            Map<Class<?>, MessageType> CACHE = new ConcurrentHashMap<>();
        }
        return Holder.CACHE.computeIfAbsent(getClass(), SimpleMessageType::new);
    }

    @Override
    default List<Object> arguments() {
        var result = new ArrayList<>();
        ReflectionUtils.doWithFields(getClass(), field -> {
            field.setAccessible(true);
            result.add(field.get(this));
        });
        return Collections.unmodifiableList(result);
    }

}
