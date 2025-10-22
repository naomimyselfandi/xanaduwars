package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.message.Event;

import java.util.List;

record CustomEvent(CustomEventType type, List<Object> arguments) implements Event {}
