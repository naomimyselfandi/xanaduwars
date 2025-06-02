package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleActionMixinTest {

    private final SimpleActionMixin<Actor, List<String>> fixture = new SimpleActionMixin<>() {

        @Override
        public Name name() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<List<String>> enumerateTargets(GameState gameState) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Execution execute(Actor user, List<String> target) {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void targetType() {
        var factory = TypeFactory.defaultInstance();
        assertThat(fixture.targetType(factory)).isEqualTo(factory.constructType(new TypeReference<List<String>>() {}));
    }

}
