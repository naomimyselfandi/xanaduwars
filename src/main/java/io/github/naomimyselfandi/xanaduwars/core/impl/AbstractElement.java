package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.naomimyselfandi.xanaduwars.core.message.*;
import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.*;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
abstract class AbstractElement implements ContextualRuleSource {

    @Getter
    @JsonIgnore
    private @NotNull GameState gameState;

    @Override
    @JsonIgnore
    public Stream<Rule> getContextualRules() {
        return getAssociatedObjects().flatMap(ContextualRuleSource::getContextualRules);
    }

    abstract Stream<ContextualRuleSource> getAssociatedObjects();

    <T> T evaluate(Query<T> query) {
        if (gameState != null) {
            return gameState.evaluate(query);
        } else {
            var name = query.type().name();
            throw new IllegalStateException("Evaluated %s query before initialization.".formatted(name));
        }
    }

    void dispatch(Event event) {
        if (gameState != null) gameState.dispatch(event);
    }

    void clearQueryCache() {
        if (gameState != null) gameState.clearQueryCache();
    }

    public void initialize(GameState gameState) {
        if (this.gameState == null) {
            this.gameState = Objects.requireNonNull(gameState);
        } else {
            throw new IllegalStateException("%s is already initialized!".formatted(this));
        }
    }

}
