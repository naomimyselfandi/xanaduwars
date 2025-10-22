package io.github.naomimyselfandi.xanaduwars.core.message;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface Message {

    MessageType type();

    @Unmodifiable List<Object> arguments();

}
