package io.github.naomimyselfandi.xanaduwars.core.actions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleActionMixinTest {

    private final SimpleActionMixin<Element, List<String>> fixture = new SimpleActionMixin<>() {

        @Override
        public Name name() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<List<String>> enumerateTargets(GameState gameState) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TagSet tags() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Execution execute(Element user, List<String> target) {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void targetType() {
        var factory = TypeFactory.defaultInstance();
        assertThat(fixture.targetType(factory)).isEqualTo(factory.constructType(new TypeReference<List<String>>() {}));
    }

}
