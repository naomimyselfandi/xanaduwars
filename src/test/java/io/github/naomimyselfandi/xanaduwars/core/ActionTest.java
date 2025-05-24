package io.github.naomimyselfandi.xanaduwars.core;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActionTest {

    private final Action<Unit, Object> fixture = new Action<>() {

        @Override
        public Name name() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TagSet tags() {
            throw new UnsupportedOperationException();
        }

        @Override
        public JavaType targetType(TypeFactory typeFactory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<Object> enumerateTargets(GameState gameState) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Execution execute(Unit user, Object target) {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void test() {
        assertThat(fixture.test(mock(), mock())).isTrue();
    }

    @EnumSource
    @ParameterizedTest
    void cost(Resource resource) {
        assertThat(fixture.cost(resource, mock(), mock())).isEqualTo(0);
    }

}
