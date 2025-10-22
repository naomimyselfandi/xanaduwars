package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.message.MessageType;
import io.github.naomimyselfandi.xanaduwars.core.script.Function;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;

/// A message type derived from a Java record type.
public record SimpleMessageType(Class<?> type) implements MessageType {

    @Override
    public String name() {
        return type.getSimpleName().replaceFirst("Event$|Query$", "");
    }

    @Override
    public List<String> properties() {
        return Arrays.stream(type.getRecordComponents()).map(RecordComponent::getName).toList();
    }

    @Override
    public Object call(@Nullable Object... arguments) {
        try {
            var constructor = type.getDeclaredConstructors()[0];
            ReflectionUtils.makeAccessible(constructor);
            return constructor.newInstance(arguments);
        } catch (ReflectiveOperationException | RuntimeException e) {
            var message = "Failed creating %s from %s.".formatted(type.getSimpleName(), Arrays.toString(arguments));
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public String toString() {
        return name();
    }

}
