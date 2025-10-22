package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.message.MessageType;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

record CustomQueryType(String name, @Unmodifiable List<String> properties, Script defaultValue) implements MessageType {

    CustomQueryType(String name, List<String> properties, Script defaultValue) {
        this.name = name;
        this.properties = List.copyOf(properties);
        this.defaultValue = defaultValue;
    }

    @Override
    @SuppressWarnings("Java9CollectionFactory")
    public Object call(@Nullable Object... arguments) {
        return new CustomQuery(this, Collections.unmodifiableList(Arrays.asList(arguments)));
    }

    @Override
    public String toString() {
        return name;
    }

}
