package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.message.MessageType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

record CustomEventType(String name, @Unmodifiable List<String> properties) implements MessageType {

    CustomEventType(String name, List<String> properties) {
        this.name = name;
        this.properties = List.copyOf(properties);
    }

    @Override
    @SuppressWarnings("Java9CollectionFactory")
    public Object call(@Nullable Object... arguments) {
        return new CustomEvent(this, Collections.unmodifiableList(Arrays.asList(arguments)));
    }

    @Override
    public String toString() {
        return name;
    }

}
