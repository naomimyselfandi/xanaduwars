package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Action;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

record Command(Element actor, @Unmodifiable List<Item> items) {

    Command(Element actor, List<Item> items) {
        this.actor = actor;
        this.items = List.copyOf(items);
    }

    record Item(Action action, @Unmodifiable List<Object> targets) {

        Item(Action action, List<Object> targets) {
            this.action = action;
            this.targets = List.copyOf(targets);
        }

    }

}
